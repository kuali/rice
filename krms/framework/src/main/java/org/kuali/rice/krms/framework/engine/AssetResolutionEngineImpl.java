package org.kuali.rice.krms.framework.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krms.api.Asset;
import org.kuali.rice.krms.api.AssetResolutionEngine;
import org.kuali.rice.krms.api.AssetResolutionException;
import org.kuali.rice.krms.api.AssetResolver;

public class AssetResolutionEngineImpl implements AssetResolutionEngine {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetResolutionEngineImpl.class);
	
	private final Map<Asset, List<AssetResolver<?>>> assetResolversByOutput = new HashMap<Asset, List<AssetResolver<?>>>();
	private final Map<AssetResolverKey, AssetResolver<?>> assetResolversByKey = new HashMap<AssetResolverKey, AssetResolver<?>>(); 
	
	// should this use soft refs?  Will require some refactoring to check if the referenced object is around;
	private final Map<Asset, Object> assetCache = new HashMap<Asset, Object>();

	@Override
	public void addAssetValue(Asset asset, Object value) {
		assetCache.put(asset, value);
	}
	
	@Override
	public void addAssetResolver(AssetResolver<?> assetResolver) {
		if (assetResolver == null) throw new IllegalArgumentException("assetResolver is reuqired");
		if (assetResolver.getOutput() == null) throw new IllegalArgumentException("assetResolver.getOutput() must not be null");

		List<AssetResolver<?>> assetResolvers = assetResolversByOutput.get(assetResolver.getOutput());
		if (assetResolvers == null) {
			assetResolvers = new LinkedList<AssetResolver<?>>();
			assetResolversByOutput.put(assetResolver.getOutput(), assetResolvers);
		}
		assetResolversByKey.put(new AssetResolverKey(assetResolver), assetResolver);
		assetResolvers.add(assetResolver);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T resolveAsset(final Asset asset) throws AssetResolutionException {
		LOG.debug("+--> resolveAsset(" + asset + ")");
		if (assetCache.containsKey(asset)) return (T)assetCache.get(asset);
		
		List<AssetResolverKey> resolutionPlan = buildAssetResolutionPlan(asset);
		// TODO: could cache these plans somewhere, since future agendas will likely require the same plans
		
		LOG.debug("resolutionPlan: " + (resolutionPlan == null ? "null" : StringUtils.join(resolutionPlan.iterator(), ", ")));
		
		if (resolutionPlan != null) {
			LOG.debug("executing plan");
			for (AssetResolverKey resolverKey : resolutionPlan) {
				AssetResolver<?> resolver = assetResolversByKey.get(resolverKey);
				
				// build prereqs
				Map<Asset, Object> resolvedPrereqs = new HashMap<Asset, Object>();
				
				// Am I making unsafe assumptions?  
				// The plan order should guarantee these prereqs exist and are cached.
				for (Asset prereq : resolver.getPrerequisites()) {
					Object resolvedPrereq = assetCache.get(prereq);
					resolvedPrereqs.put(prereq, resolvedPrereq);
				}
				
				Object resolvedAsset = resolver.resolve(resolvedPrereqs);
				assetCache.put(resolver.getOutput(), resolvedAsset);
			}		
		} else {
			throw new AssetResolutionException("Unable to plan the resolution of asset " + asset);
		}
		return (T)assetCache.get(asset);
	}
	
	// TODO: try to extract some methods out to shorten this beast
	protected List<AssetResolverKey> buildAssetResolutionPlan(Asset asset) {
		// our result
		List<AssetResolverKey> resolutionPlan = null;
		
		// Holds the resolvers we've visited, along with the needed metadata for generating our final plan
		Map<AssetResolverKey, Visited> visitedByKey = new HashMap<AssetResolverKey, Visited>();
		
		// this holds a least cost first list of nodes remaining to be explored
		PriorityQueue<ToVisit> toVisits = new PriorityQueue<ToVisit>(); // nice grammar there cowboy
		
		// dummy resolver to be the root of this tree
		// Do I really need this?  Yes, because there may be more than one resolver that resolves to the desired asset,
		// so this destination unifies the trees of those candidate resolvers
		AssetResolver destination = createDestination(asset); // problem is we can't get this one out of the registry
		AssetResolverKey destinationKey = new AssetResolverKey(destination);
		
		LOG.debug("Beginning resolution tree search for " + asset);
		
		// seed our queue of resolvers to visit
		// need to be aware of null parent for root ToVisit
		toVisits.add(new ToVisit(0, destination, null));
		
		// there may not be a viable plan
		boolean plannedToDestination = false;
		
		// We'll do a modified Dijkstra's shortest path algorithm, where at each leaf we see if we've planned out
		// asset resolution all the way up to the root, our destination.  If so, we just reconstruct our plan.
		while (!plannedToDestination && toVisits.size() > 0) {
			// visit least cost node remaining
			ToVisit visiting = toVisits.poll();

			LOG.debug("visiting " + visiting.getAssetResolverKey());
			
			// the resolver is the edge in our tree -- we don't get it directly from the assetResolversByKey Map, because it could be our destination
			AssetResolver resolver = getResolver(visiting.getAssetResolverKey(), destination, destinationKey);
			AssetResolver parent = getResolver(visiting.getParentKey(), destination, destinationKey);
			
			if (visitedByKey.containsKey(visiting.getAssetResolverKey())) {
				continue; // TODO: is this right?  will this make trouble?  We've already visited this one
			}
			
			Visited parentVisited = visitedByKey.get(visiting.getParentKey());
			
			if (resolver == null) throw new RuntimeException("Unable to get AssetResolver by its key");
			Set<Asset> prereqs = resolver.getPrerequisites();
			// keep track of any prereqs that we already have handy
			List<Asset> metPrereqs = new LinkedList<Asset>();
			
			// see what prereqs we have already, and which we'll need to visit
			if (prereqs != null) for (Asset prereq : prereqs) {
				if (!assetCache.containsKey(prereq)) {
					// enqueue all resolvers in toVisits
					List<AssetResolver<?>> prereqResolvers = assetResolversByOutput.get(prereq);
					if (prereqResolvers != null) for (AssetResolver prereqResolver : prereqResolvers) {
						toVisits.add(new ToVisit(visiting.getCost() /* cost to get to this resolver */, prereqResolver, resolver));
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
			resolutionPlan = new LinkedList<AssetResolverKey>();
			
			assembleLinearResolutionPlan(visitedByKey.get(destinationKey), visitedByKey, resolutionPlan);
		}
		return resolutionPlan;
	}
	
	/**
	 *  @return the Visited object for the resolver we just, er, well, visited.
	 */
	private Visited buildVisited(AssetResolver resolver, Visited parentVisited, Collection<Asset> metPrereqs) {
		Visited visited = null;
		
		List<AssetResolverKey> pathTo = new ArrayList<AssetResolverKey>(1 + (parentVisited == null ? 0 : parentVisited.pathTo.size()));
		if (parentVisited != null && parentVisited.getPathTo() != null) pathTo.addAll(parentVisited.getPathTo());
		if (parentVisited != null) pathTo.add(parentVisited.getResolverKey());
		AssetResolverKey resolverKey = new AssetResolverKey(resolver);
		
		visited = new Visited(resolverKey, pathTo, resolver.getPrerequisites(), resolver.getCost() + (parentVisited == null ? 0 : parentVisited.getCost()));
		for (Asset metPrereq : metPrereqs) { visited.addPlannedPrereq(metPrereq); }
	
		return visited;
	}

	/**
	 * our dummy destination isn't allowed to pollute assetResolversByKey, hence the ugly conditional encapsulated herein
	 */
	private AssetResolver getResolver(AssetResolverKey resolverKey,
			AssetResolver destination, AssetResolverKey destinationKey) {
		AssetResolver resolver;
		if (destinationKey.equals(resolverKey)) {
			resolver = destination;
		} else {
			resolver = assetResolversByKey.get(resolverKey);
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
			AssetResolverKey destinationKey,
			Map<AssetResolverKey, Visited> visitedByKey) {
		boolean plannedToDestination = false;
		if (visited.isFullyPlanned()) {
			LOG.debug("Leaf! this resolver's prereqs are all avialable.");
			// no traversing further yet, instead we need to check how far up the tree is fully planned out 
			// step backwards toward the root of the tree and see if we're good all the way to our objective.
			// if a node fully planned, move up the tree (towards the root) to see if its parent is fully planned, and so on.
			
			if (visited.getPathTo().size() > 0) {
				// reverse the path to
				List<AssetResolverKey> reversePathTo = new ArrayList<AssetResolverKey>(visited.getPathTo());
				Collections.reverse(reversePathTo);
				
				// we use this to propagate resolutions up the tree
				Visited previousAncestor = visited;
				
				for (AssetResolverKey ancestorKey : reversePathTo) {
					
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
	
	private void assembleLinearResolutionPlan(Visited visited, Map<AssetResolverKey, Visited> visitedByKey, List<AssetResolverKey> plan) {
		// DFS
		for (AssetResolverKey prereqResolverKey : visited.getPrereqResolvers()) {
			Visited prereqVisited = visitedByKey.get(prereqResolverKey);
			assembleLinearResolutionPlan(prereqVisited, visitedByKey, plan);
			plan.add(prereqResolverKey);
		}
	}

	/**
	 * Create our dummy destination resolver
	 * @param asset
	 */
	private AssetResolver<? extends Object> createDestination(final Asset asset) {
		AssetResolver<?> destination = new AssetResolver<Object>() {
			final Asset dest = new Asset("", "");
			@Override
			public int getCost() {
				return 0;
			}
			@Override
			public Asset getOutput() {
				return dest;
			}
			@Override
			public Set<Asset> getPrerequisites() {
				return Collections.<Asset>singleton(asset);
			}
			@Override
			public Object resolve(Map<Asset, Object> resolvedPrereqs) throws AssetResolutionException {
				return null;
			}
		};
		return destination;
	}
	
	
	private static class ToVisit implements Comparable<ToVisit> {
		
		private final int precost;
		private final int addcost;
		private final AssetResolverKey resolverKey;
		
		// the parent key is not being used for comparison purposes currently
		private final AssetResolverKey parentKey;
		
		/**
		 * @param precost
		 * @param resolver
		 */
		public ToVisit(int precost, AssetResolver resolver, AssetResolver parent) {
			super();
			this.precost = precost;
			this.addcost = resolver.getCost();
			this.resolverKey = new AssetResolverKey(resolver.getOutput(), resolver.getPrerequisites());
			
			if (parent != null) {
				this.parentKey = new AssetResolverKey(parent.getOutput(), parent.getPrerequisites());
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
		
		public AssetResolverKey getAssetResolverKey() {
			return resolverKey;
		}
		
		public AssetResolverKey getParentKey() {
			return parentKey;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+"("+getAssetResolverKey()+")";
		}
	}
	
	protected static class AssetResolverKey implements Comparable<AssetResolverKey> {
		private final List<Asset> data;
		
		// just used for toArray call
		private static final Asset[] TYPER = new Asset[0];
		
		public AssetResolverKey(AssetResolver resolver) {
			this(resolver.getOutput(), resolver.getPrerequisites());
		}
		
		private AssetResolverKey(Asset dest, Set<Asset> prereqs) {
			if (dest == null) throw new IllegalArgumentException("dest parameter must not be null");
			data = new ArrayList<Asset>(1 + ((prereqs == null) ? 0 : prereqs.size())); // size ArrayList perfectly
			data.add(dest);
			if (prereqs != null) {
				// this is painful, but to be able to compare we need a defined order 
				Asset [] prereqsArray = prereqs.toArray(TYPER);
				Arrays.sort(prereqsArray);
				for (Asset prereq : prereqsArray) {
					data.add(prereq);
				}
			}
		}
		
		public Asset getOutput() {
			return data.get(0);
		}
		
		private String comparatorHelperMemo = null;
		
		private String getComparatorHelper() {
			if (comparatorHelperMemo == null) {
				StringBuilder sb = new StringBuilder();
				for (int i=0; i<data.size(); i++) {
					Asset asset = data.get(i);
					if (i == 1) {
						sb.append(" <- ");
					} else if (i > 1) {
						sb.append(",");
					}
					if (asset != null) {
						sb.append(asset.getComparatorHelper());
					}
				}
				comparatorHelperMemo = sb.toString();
			}
			return comparatorHelperMemo;
		}
			
		@Override
		public int compareTo(AssetResolverKey o) {
			if (o == null) return 1;
			return getComparatorHelper().compareTo(o.getComparatorHelper());
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
			AssetResolverKey other = (AssetResolverKey) obj;
			return this.compareTo(other) == 0;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+"("+getComparatorHelper()+")";
		}
	}
	
	
	private static class Visited {
		private AssetResolverKey resolverKey; 
		private List<AssetResolverKey> pathTo;
		private Set<Asset> remainingPrereqs;
		private Map<Asset, AssetResolverKey> prereqResolvers;
		private int cost;
		
		/**
		 * @param resolver
		 * @param pathTo
		 * @param fullyPlanned
		 * @param cost
		 */
		public Visited(AssetResolverKey resolverKey, List<AssetResolverKey> pathTo, Set<Asset> prereqs, int cost) {
			super();
			this.resolverKey = resolverKey;
			this.pathTo = pathTo;
			this.remainingPrereqs = new HashSet<Asset>(prereqs);
			this.prereqResolvers = new HashMap<Asset, AssetResolverKey>();
			this.cost = cost;
		}

		/**
		 * @return the path from the goal node down to this resolver
		 */
		public List<AssetResolverKey> getPathTo() {
			return pathTo;
		}
		
		public AssetResolverKey getResolverKey() {
			return resolverKey;
		}
		
		public Collection<AssetResolverKey> getPrereqResolvers() {
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
		
		public void addPlannedPrereq(AssetResolverKey assetResolverKey) {
			remainingPrereqs.remove(assetResolverKey.getOutput());
			prereqResolvers.put(assetResolverKey.getOutput(), assetResolverKey);
		}
		
		public void addPlannedPrereq(Asset asset) {
			remainingPrereqs.remove(asset);
		}
	}
	
	private static class InvalidResolutionPathException extends Exception {
		private static final long serialVersionUID = 1L;

		public InvalidResolutionPathException() {
		}
	}	

}


