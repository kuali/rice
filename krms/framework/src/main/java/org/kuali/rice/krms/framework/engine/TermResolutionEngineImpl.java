package org.kuali.rice.krms.framework.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krms.api.TermResolutionEngine;
import org.kuali.rice.krms.api.TermResolver;
import org.kuali.rice.krms.api.Term;
import org.kuali.rice.krms.api.TermResolutionException;
import org.kuali.rice.krms.api.TermSpecification;

public class TermResolutionEngineImpl implements TermResolutionEngine {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TermResolutionEngineImpl.class);
	
	private final Map<TermSpecification, List<TermResolver<?>>> termResolversByOutput = new HashMap<TermSpecification, List<TermResolver<?>>>();
	private final Map<TermResolverKey, TermResolver<?>> termResolversByKey = new HashMap<TermResolverKey, TermResolver<?>>(); 
	
	// should this use soft refs?  Will require some refactoring to check if the referenced object is around;
	private final Map<Term, Object> termCache = new HashMap<Term, Object>();

	@Override
	public void addTermValue(Term term, Object value) {
		termCache.put(term, value);
	}
	
	@Override
	public void addTermResolver(TermResolver<?> termResolver) {
		if (termResolver == null) throw new IllegalArgumentException("termResolver is reuqired");
		if (termResolver.getOutput() == null) throw new IllegalArgumentException("termResolver.getOutput() must not be null");

		List<TermResolver<?>> termResolvers = termResolversByOutput.get(termResolver.getOutput());
		if (termResolvers == null) {
			termResolvers = new LinkedList<TermResolver<?>>();
			termResolversByOutput.put(termResolver.getOutput(), termResolvers);
		}
		termResolversByKey.put(new TermResolverKey(termResolver), termResolver);
		termResolvers.add(termResolver);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T resolveTerm(Term term) throws TermResolutionException {
		LOG.debug("+--> resolveTerm(" + term + ")");
		if (termCache.containsKey(term)) return (T)termCache.get(term);
		
		TermSpecification termSpec = term.getSpecification();
		
		// build plan w/ term spec for correct TermResolver selection
		List<TermResolverKey> resolutionPlan = buildTermResolutionPlan(termSpec);
		// TODO: could cache these plans somewhere, since future agendas will likely require the same plans
		
		LOG.debug("resolutionPlan: " + (resolutionPlan == null ? "null" : StringUtils.join(resolutionPlan.iterator(), ", ")));
		
		if (resolutionPlan != null) {
			LOG.debug("executing plan");
			for (TermResolverKey resolverKey : resolutionPlan) {
				TermResolver<?> resolver = termResolversByKey.get(resolverKey);
				
				// build prereqs
				Map<TermSpecification, Object> resolvedPrereqs = new HashMap<TermSpecification, Object>();
				
				// The plan order should guarantee these prereqs exist and are cached.
				for (TermSpecification prereq : resolver.getPrerequisites()) {
					Object resolvedPrereq = termCache.get(new Term(prereq, null));
					resolvedPrereqs.put(prereq, resolvedPrereq);
				}
				
				Map<String, String> providedParameters = Collections.emptyMap();
				// The destination Term (and only the dest Term) can be parameterized
				if (termSpec.equals(resolver.getOutput())) {
					providedParameters = term.getParameters();
					
					// throw a TermResolutionException if the params doen't match up
					validateTermParameters(resolver, providedParameters);
				}
				
				Object resolvedTerm = resolver.resolve(resolvedPrereqs, providedParameters);
				if (termSpec.equals(resolver.getOutput())) {
					termCache.put(term, resolvedTerm);
				} else {
					if (!CollectionUtils.isEmpty(resolver.getParameterNames())) {
						// Shouldn't happen due to checks in buildResolutionPlan
						throw new TermResolutionException("TermResolvers requiring parameters cannot be intermediates in the Term resolution plan");
					}
					termCache.put(new Term(resolver.getOutput(), null), resolvedTerm);
				}
			}		
		} else {
			throw new TermResolutionException("Unable to plan the resolution of " + term);
		}
		return (T)termCache.get(term);
	}

	/**
	 * This method ...
	 * 
	 * @param resolver
	 * @param providedParameters
	 * @throws TermResolutionException
	 */
	private void validateTermParameters(TermResolver<?> resolver,
			Map<String, String> providedParameters)
			throws TermResolutionException {
		// validate that params match what the TermResolver requires
		if (!providedParameters.keySet().equals(resolver.getParameterNames())) {
			StringBuilder sb = new StringBuilder();
			
			boolean first = true;
			for (Entry<String,String> param : providedParameters.entrySet()) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(param.getKey());
				sb.append("=");
				sb.append(param.getValue());
			}
			
			throw new TermResolutionException("provided parameters ("+ sb 
					+") do not match requirements ("
					+ StringUtils.join(resolver.getParameterNames(), ",") +")");
		}
	}
	
	protected List<TermResolverKey> buildTermResolutionPlan(TermSpecification term) {
		// our result
		List<TermResolverKey> resolutionPlan = null;
		
		// Holds the resolvers we've visited, along with the needed metadata for generating our final plan
		Map<TermResolverKey, Visited> visitedByKey = new HashMap<TermResolverKey, Visited>();
		
		// this holds a least cost first list of nodes remaining to be explored
		PriorityQueue<ToVisit> toVisits = new PriorityQueue<ToVisit>(); // nice grammar there cowboy
		
		// dummy resolver to be the root of this tree
		// Do I really need this?  Yes, because there may be more than one resolver that resolves to the desired term,
		// so this destination unifies the trees of those candidate resolvers
		TermResolver destination = createDestination(term); // problem is we can't get this one out of the registry
		TermResolverKey destinationKey = new TermResolverKey(destination);
		
		LOG.debug("Beginning resolution tree search for " + term);
		
		// seed our queue of resolvers to visit
		// need to be aware of null parent for root ToVisit
		toVisits.add(new ToVisit(0, destination, null));
		
		// there may not be a viable plan
		boolean plannedToDestination = false;
		
		// We'll do a modified Dijkstra's shortest path algorithm, where at each leaf we see if we've planned out
		// term resolution all the way up to the root, our destination.  If so, we just reconstruct our plan.
		while (!plannedToDestination && toVisits.size() > 0) {
			// visit least cost node remaining
			ToVisit visiting = toVisits.poll();

			LOG.debug("visiting " + visiting.getTermResolverKey());
			
			// the resolver is the edge in our tree -- we don't get it directly from the termResolversByKey Map, because it could be our destination
			TermResolver resolver = getResolver(visiting.getTermResolverKey(), destination, destinationKey);
			TermResolver parent = getResolver(visiting.getParentKey(), destination, destinationKey);
			
			if (visitedByKey.containsKey(visiting.getTermResolverKey())) {
				continue; // We've already visited this one
			}
			
			Visited parentVisited = visitedByKey.get(visiting.getParentKey());
			
			if (resolver == null) throw new RuntimeException("Unable to get TermResolver by its key");
			Set<TermSpecification> prereqs = resolver.getPrerequisites();
			// keep track of any prereqs that we already have handy
			List<TermSpecification> metPrereqs = new LinkedList<TermSpecification>();
			
			// see what prereqs we have already, and which we'll need to visit
			if (prereqs != null) for (TermSpecification prereq : prereqs) {
				if (!termCache.containsKey(new Term(prereq, null))) {
					// enqueue all resolvers in toVisits
					List<TermResolver<?>> prereqResolvers = termResolversByOutput.get(prereq);
					if (prereqResolvers != null) for (TermResolver prereqResolver : prereqResolvers) {
						// Only TermResolvers that don't take paramaterized terms can be chained, so:
						// if the TermResolver doesn't take parameters, or it resolves the output term
						if (CollectionUtils.isEmpty(prereqResolver.getParameterNames()) || 
								term.equals(prereqResolver.getOutput())) 
						{
							// queue it up for visiting
							toVisits.add(new ToVisit(visiting.getCost() /* cost to get to this resolver */, prereqResolver, resolver));
						}
					}
				} else {
					metPrereqs.add(prereq);
				}
			}
			
			// Build visited info
			Visited visited = buildVisited(resolver, parentVisited, metPrereqs);
			visitedByKey.put(visited.getResolverKey(), visited);
			
			plannedToDestination = isPlannedBackToDestination(visited, destinationKey, visitedByKey);
		}
		
		if (plannedToDestination) {
			// build result from Visited tree.
			resolutionPlan = new LinkedList<TermResolverKey>();
			
			assembleLinearResolutionPlan(visitedByKey.get(destinationKey), visitedByKey, resolutionPlan);
		}
		return resolutionPlan;
	}
	
	/**
	 *  @return the Visited object for the resolver we just, er, well, visited.
	 */
	private Visited buildVisited(TermResolver resolver, Visited parentVisited, Collection<TermSpecification> metPrereqs) {
		Visited visited = null;
		
		List<TermResolverKey> pathTo = new ArrayList<TermResolverKey>(1 + (parentVisited == null ? 0 : parentVisited.pathTo.size()));
		if (parentVisited != null && parentVisited.getPathTo() != null) pathTo.addAll(parentVisited.getPathTo());
		if (parentVisited != null) pathTo.add(parentVisited.getResolverKey());
		TermResolverKey resolverKey = new TermResolverKey(resolver);
		
		visited = new Visited(resolverKey, pathTo, resolver.getPrerequisites(), resolver.getCost() + (parentVisited == null ? 0 : parentVisited.getCost()));
		for (TermSpecification metPrereq : metPrereqs) { visited.addPlannedPrereq(metPrereq); }
	
		return visited;
	}

	/**
	 * our dummy destination isn't allowed to pollute termResolversByKey, hence the ugly conditional encapsulated herein
	 */
	private TermResolver getResolver(TermResolverKey resolverKey,
			TermResolver destination, TermResolverKey destinationKey) {
		TermResolver resolver;
		if (destinationKey.equals(resolverKey)) {
			resolver = destination;
		} else {
			resolver = termResolversByKey.get(resolverKey);
		}
		return resolver;
	}

	/**
	 * @param visited
	 * @param destinationKey
	 * @param visitedByKey
	 * @param plannedToDestination
	 * @return
	 */
	private boolean isPlannedBackToDestination(Visited visited,
			TermResolverKey destinationKey,
			Map<TermResolverKey, Visited> visitedByKey) {
		boolean plannedToDestination = false;
		if (visited.isFullyPlanned()) {
			LOG.debug("Leaf! this resolver's prereqs are all avialable.");
			// no traversing further yet, instead we need to check how far up the tree is fully planned out 
			// step backwards toward the root of the tree and see if we're good all the way to our objective.
			// if a node fully planned, move up the tree (towards the root) to see if its parent is fully planned, and so on.
			
			if (visited.getPathTo().size() > 0) {
				// reverse the path to
				List<TermResolverKey> reversePathTo = new ArrayList<TermResolverKey>(visited.getPathTo());
				Collections.reverse(reversePathTo);
				
				// we use this to propagate resolutions up the tree
				Visited previousAncestor = visited;
				
				for (TermResolverKey ancestorKey : reversePathTo) {
					
					Visited ancestorVisited = visitedByKey.get(ancestorKey);
					ancestorVisited.addPlannedPrereq(previousAncestor.getResolverKey());
					
					LOG.debug("checking ancestor " + ancestorKey);
					
					if (ancestorVisited.isFullyPlanned() && ancestorKey.equals(destinationKey)) {
						// Woot! Job's done!
						plannedToDestination = true;
						break;
					} else if (!ancestorVisited.isFullyPlanned()) { // if the ancestor isn't fully planned, we're done
						LOG.debug("Still have planning to do.");
						break;
					}
					// update previousAncestor reference for next iteration
					previousAncestor = ancestorVisited;
				}
			} else {
				// we're done already! do a jig?
				LOG.debug("Trivial plan.");
				plannedToDestination = true;
			}
		}
		return plannedToDestination;
	}
	
	private void assembleLinearResolutionPlan(Visited visited, Map<TermResolverKey, Visited> visitedByKey, List<TermResolverKey> plan) {
		// DFS
		for (TermResolverKey prereqResolverKey : visited.getPrereqResolvers()) {
			Visited prereqVisited = visitedByKey.get(prereqResolverKey);
			assembleLinearResolutionPlan(prereqVisited, visitedByKey, plan);
			plan.add(prereqResolverKey);
		}
	}

	/**
	 * Create our dummy destination resolver
	 * @param term
	 */
	private TermResolver<? extends Object> createDestination(final TermSpecification term) {
		TermResolver<?> destination = new TermResolver<Object>() {
			final TermSpecification dest = new TermSpecification("", "");
			@Override
			public int getCost() {
				return 0;
			}
			@Override
			public TermSpecification getOutput() {
				return dest;
			}
			@Override
			public Set<TermSpecification> getPrerequisites() {
				return Collections.<TermSpecification>singleton(term);
			}
			@Override
			public Set<String> getParameterNames() {
				return Collections.emptySet();
			}
			@Override
			public Object resolve(Map<TermSpecification, Object> resolvedPrereqs, Map<String, String> parameters) throws TermResolutionException {
				return null;
			}
		};
		return destination;
	}
	
	
	private static class ToVisit implements Comparable<ToVisit> {
		
		private final int precost;
		private final int addcost;
		private final TermResolverKey resolverKey;
		
		// the parent key is not being used for comparison purposes currently
		private final TermResolverKey parentKey;
		
		/**
		 * @param precost
		 * @param resolver
		 */
		public ToVisit(int precost, TermResolver resolver, TermResolver parent) {
			super();
			this.precost = precost;
			this.addcost = resolver.getCost();
			this.resolverKey = new TermResolverKey(resolver);
			
			if (parent != null) {
				this.parentKey = new TermResolverKey(parent);
			} else {
				this.parentKey = null;
			}
		}

		public int getCost() {
			return precost + addcost;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass())
				return false;			
			ToVisit other = (ToVisit)obj;
			return this.compareTo(other) == 0;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getCost();
			result = prime * result + ((resolverKey == null) ? 0 : resolverKey.hashCode());
			return result;
		}
		
		/**
		 * {@inheritDoc Comparable}
		 */
		@Override
		public int compareTo(ToVisit o) {
			if (o == null) return 1;
			if (getCost() > o.getCost()) return 1;
			if (getCost() < o.getCost()) return -1;
			return resolverKey.compareTo(o.resolverKey);
		}
		
		public TermResolverKey getTermResolverKey() {
			return resolverKey;
		}
		
		public TermResolverKey getParentKey() {
			return parentKey;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+"("+getTermResolverKey()+")";
		}
	}
	
	protected static class TermResolverKey implements Comparable<TermResolverKey> {
		private final List<TermSpecification> data;
		private final String [] params;
		
		// just used for toArray call
		private static final TermSpecification[] TERM_SPEC_TYPER = new TermSpecification[0];
		private static final String[] STRING_TYPER = new String[0];
		
		public TermResolverKey(TermResolver resolver) {
			this(resolver.getOutput(), resolver.getParameterNames(), resolver.getPrerequisites());
		}
		
		private TermResolverKey(TermSpecification dest, Set<String> paramSet, Set<TermSpecification> prereqs) {
			if (dest == null) throw new IllegalArgumentException("dest parameter must not be null");
			data = new ArrayList<TermSpecification>(1 + ((prereqs == null) ? 0 : prereqs.size())); // size ArrayList perfectly
			data.add(dest);
			if (!CollectionUtils.isEmpty(paramSet)) {
				params = paramSet.toArray(STRING_TYPER);
				Arrays.sort(params);
			} else {
				params = STRING_TYPER; // just assigning a handy zero length String array
			}
			if (prereqs != null) {
				// this is painful, but to be able to compare we need a defined order 
				TermSpecification [] prereqsArray = prereqs.toArray(TERM_SPEC_TYPER);
				Arrays.sort(prereqsArray);
				for (TermSpecification prereq : prereqsArray) {
					data.add(prereq);
				}
			}
		}
		
		public TermSpecification getOutput() {
			return data.get(0);
		}
			
		@Override
		public int compareTo(TermResolverKey o) {
			if (o == null) return 1;
			
			Iterator<TermSpecification> mDataIter = data.iterator();
			Iterator<TermSpecification> oDataIter = o.data.iterator();
			
			while (mDataIter.hasNext() && oDataIter.hasNext()) {
				int itemCompareResult = mDataIter.next().compareTo(oDataIter.next());
				if (itemCompareResult != 0) return itemCompareResult;
			}
			
			if (mDataIter.hasNext()) return 1;
			if (oDataIter.hasNext()) return -1;
			
			return 0;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return data.hashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TermResolverKey other = (TermResolverKey) obj;
			return this.compareTo(other) == 0;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append("(");
			Iterator<TermSpecification> iter = data.iterator();
			sb.append(iter.next().toString());
			if (params.length != 0) {
				sb.append("+");
				ArrayUtils.toString(params);
			}
			
			if (iter.hasNext()) sb.append(" <- ");
			boolean first = true;
			while (iter.hasNext()) {
				if (first) first = false;
				else sb.append(", ");
				sb.append(iter.next().toString());
			}
			
			
			return sb.toString();
		}
	}
	
	
	private static class Visited {
		private TermResolverKey resolverKey; 
		private List<TermResolverKey> pathTo;
		private Set<TermSpecification> remainingPrereqs;
		private Map<TermSpecification, TermResolverKey> prereqResolvers;
		private int cost;
		
		/**
		 * @param resolver
		 * @param pathTo
		 * @param fullyPlanned
		 * @param cost
		 */
		public Visited(TermResolverKey resolverKey, List<TermResolverKey> pathTo, Set<TermSpecification> prereqs, int cost) {
			super();
			this.resolverKey = resolverKey;
			this.pathTo = pathTo;
			this.remainingPrereqs = new HashSet<TermSpecification>(prereqs);
			this.prereqResolvers = new HashMap<TermSpecification, TermResolverKey>();
			this.cost = cost;
		}

		/**
		 * @return the path from the goal node down to this resolver
		 */
		public List<TermResolverKey> getPathTo() {
			return pathTo;
		}
		
		public TermResolverKey getResolverKey() {
			return resolverKey;
		}
		
		public Collection<TermResolverKey> getPrereqResolvers() {
			return prereqResolvers.values();
		}
		
		/**
		 * @return true if resolution of all the prerequisites has been planned
		 */
		public boolean isFullyPlanned() {
			return remainingPrereqs.isEmpty();
		}
		
		public int getCost() {
			return cost;
		}
		
		public void addPlannedPrereq(TermResolverKey termResolverKey) {
			remainingPrereqs.remove(termResolverKey.getOutput());
			prereqResolvers.put(termResolverKey.getOutput(), termResolverKey);
		}
		
		public void addPlannedPrereq(TermSpecification termSpec) {
			remainingPrereqs.remove(termSpec);
		}
	}
	
	private static class InvalidResolutionPathException extends Exception {
		private static final long serialVersionUID = 1L;

		public InvalidResolutionPathException() {
		}
	}	

}


