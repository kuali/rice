package org.kuali.rice.krms.framework;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krms.api.Asset;
import org.kuali.rice.krms.api.AssetResolutionEngine;
import org.kuali.rice.krms.api.AssetResolutionException;
import org.kuali.rice.krms.api.AssetResolver;
import org.kuali.rice.krms.framework.engine.AssetResolutionEngineImpl;


public class AssetResolutionEngineTest {
	
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetResolutionEngineTest.class);

	private AssetResolutionEngine assetResolutionService = null;
	
	@Before
	public void setUp() {
		assetResolutionService = new AssetResolutionEngineImpl();
	}
	
	@Test
	public void testNoResolution() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);
		
		// GIVENS:
		testHelper.addGivens("A");

		testHelper.logScenarioDescription();
		
		testHelper.assertSuccess("A");
	}
	
	@Test
	public void testSimpleResolution() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);
		
		// GIVENS:
		testHelper.addGivens("A");
		
		// RESOLVERS:
		testHelper.addResolver("B", /* <-- */ "A");
		
		testHelper.logScenarioDescription();
		
		testHelper.assertSuccess("B");
	}
	
	@Test
	public void testTwoStepResolution() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);
		
		// GIVENS:
		testHelper.addGivens("A");
		
		// RESOLVERS:
		testHelper.addResolver("B", /* <-- */ "A");
		testHelper.addResolver("C", /* <-- */ "B");
		
		testHelper.logScenarioDescription();
		
		testHelper.assertSuccess("C");
	}

	@Test
	public void testForkingResolution() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);

		// GIVENS:
		testHelper.addGivens("A", "Z");
		
		// RESOLVERS:
		testHelper.addResolver("D", /* <-- */ "B","C");
		testHelper.addResolver("C", /* <-- */ "Z");
		testHelper.addResolver("B", /* <-- */ "A");
		
		testHelper.logScenarioDescription();
		
		testHelper.assertSuccess("D");
	}
	
	@Test
	public void testMultipleValidPaths() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);
		
		// GIVENS:
		testHelper.addGivens("A", "Z");
		
		// RESOLVERS:
		testHelper.addResolver("D", /* <-- */ "B","C");
		testHelper.addResolver("C", /* <-- */ "Z");
		testHelper.addResolver("B", /* <-- */ "A");
		testHelper.addResolver("D", /* <-- */ "A");
		
		testHelper.logScenarioDescription();
		
		testHelper.assertSuccess("D");
	}
	
	@Test
	public void testDiamond() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);

		// GIVENS:
		testHelper.addGivens("A");
		
		// RESOLVERS:
		testHelper.addResolver("D", /* <-- */ "B","C");
		testHelper.addResolver("C", /* <-- */ "A");
		testHelper.addResolver("B", /* <-- */ "A");
		
		testHelper.logScenarioDescription();
		
		testHelper.assertSuccess("D");
		
	}

	@Test
	public void testComplexPath() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);

		// GIVENS:
		testHelper.addGivens("Q","R","S");
		
		// RESOLVERS:
		testHelper.addResolver("A", /* <-- */ "B","F");
		testHelper.addResolver("A", /* <-- */ "Z");
		testHelper.addResolver("B", /* <-- */ "D");
		testHelper.addResolver("B", /* <-- */ "C");
		testHelper.addResolver("C", /* <-- */ "S");
		testHelper.addResolver("D", /* <-- */ "E");
		testHelper.addResolver("E", /* <-- */ "S");
		testHelper.addResolver("F", /* <-- */ "G","Q");
		testHelper.addResolver("G", /* <-- */ "R");
		
		testHelper.logScenarioDescription();
		
		testHelper.assertSuccess("A");
		
	}

	@Test
	public void testCycle() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);

		// GIVENS:
		// none
		
		// RESOLVERS:
		testHelper.addResolver("D", /* <-- */ "C");
		testHelper.addResolver("C", /* <-- */ "D");
		
		testHelper.logScenarioDescription();
		
		testHelper.assertException("D");
		
	}
	

	@Test
	public void testUnreachableAsset() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);

		// GIVENS:
		// none
		
		// RESOLVERS:
		testHelper.addResolver("D", /* <-- */ "C");
		testHelper.addResolver("C", /* <-- */ "B");
		
		testHelper.logScenarioDescription();
		
		testHelper.assertException("D");
		
	}
	
	@Test
	public void testRedHerringPath() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);

		// GIVENS:
		testHelper.addGivens("Q");
		
		// RESOLVERS:
		testHelper.addResolver("A", /* <-- */ "B");
		// this is the shortest path, but C can't be resolved
		testHelper.addResolver("B", /* <-- */ "C");
		
		testHelper.addResolver("B", /* <-- */ "D");
		testHelper.addResolver("D", /* <-- */ "Q");
		
		testHelper.logScenarioDescription();
		
		testHelper.assertSuccess("A");
	}
	
	private static class WhiteBoxAssetResolutionServiceImpl extends AssetResolutionEngineImpl {
		
		// expose this for testing purposes
		@Override
		public List<AssetResolverKey> buildAssetResolutionPlan(Asset asset) {
			return super.buildAssetResolutionPlan(asset);
		}
	}
	
	
	@Test
	public void testShortestPath() {
		
		WhiteBoxAssetResolutionServiceImpl whiteBoxAssetResolutionService = new WhiteBoxAssetResolutionServiceImpl();
		TestScenarioHelper testHelper = new TestScenarioHelper(whiteBoxAssetResolutionService);

		// GIVENS:
		testHelper.addGivens("Q");
		
		// RESOLVERS:
		// this one costs 3 (instead of default cost of 1)
		testHelper.addResolver(3, "A", /* <-- */ "Q");

		// the steps for the alternate path each cost 1, but total length is 4
		testHelper.addResolver("A", /* <-- */ "B");
		testHelper.addResolver("B", /* <-- */ "C");
		testHelper.addResolver("C", /* <-- */ "D");
		testHelper.addResolver("D", /* <-- */ "Q");
		
		testHelper.logScenarioDescription();
		
		List<?> plan = whiteBoxAssetResolutionService.buildAssetResolutionPlan(testHelper.getAsset("A"));
		LOG.info("resolutionPlan: " + StringUtils.join(plan, ", ") + " <-- should be length 1!");
		assertTrue("didn't choose the shortest resolution path (of length 1)", plan.size() == 1);
	}	
	
	
	/*
	 *  TODO: test exception variants:
	 *  - AssetResolver throws AssetResolutionException
	 *  - AssetResolutionService is passed a null asset
	 */
	
	// TODO: what should the AssetResolutionService do if a resolver throws a RuntimeException?
	/*
	@Test
	public void testExplodingResolver() {
		TestScenarioHelper testHelper = new TestScenarioHelper(assetResolutionService);

		// GIVENS:
		testHelper.addGivens("Q");
		
		// RESOLVERS:
		AssetResolverMock exploder = new AssetResolverMock(testHelper.getAsset("A"), testHelper.getResult("A"), testHelper.getAsset("Q"));
		exploder.setIsExploder(true);
		assetResolutionService.addAssetResolver(exploder);
		
		testHelper.logScenarioDescription();
		
		testHelper.assertException("A");
	}
	*/
	
	
	private static class AssetResolverMock<T> implements AssetResolver<T> {
		private Asset output;
		private T result;
		private Set<Asset> prereqs;
		private int cost;
		private boolean isExploder = false;
		
		public AssetResolverMock(Asset output, T result, int cost, Asset ... prereqs) {
			this.output = output;
			this.result = result;
			this.prereqs = new HashSet<Asset>(Arrays.asList(prereqs));
			this.cost = cost;
		}
		
		@Override
		public int getCost() {
			return cost;
		}
		
		@Override
		public Asset getOutput() {
			return output;
		}
		
		@Override
		public Set<Asset> getPrerequisites() {
			return prereqs;
		}
		
		public void setIsExploder(boolean isExploder) {
			this.isExploder = isExploder;
		}
		
		@Override
		public T resolve(Map<Asset, Object> resolvedPrereqs) {
			
			if (isExploder) {
				throw new RuntimeException("I'm the exploder, coo coo catchoo");
			}
			
			// get all prereqs first
			for (Asset prereq : prereqs) {
				Object result = resolvedPrereqs.get(prereq);
				if (result == null) fail("got back null for prereq " + prereq);
			}

			LOG.info("resolving " + output);
			return result;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+"[ "+output+ " <- " + StringUtils.join(prereqs.iterator(), ",") + " ]";
		}

	}
	

	private static class TestScenarioHelper {
		
		private final AssetResolutionEngine ars;
		
		private List<String> givens = new LinkedList<String>();
		private List<String> resolvers = new LinkedList<String>();

		public TestScenarioHelper(AssetResolutionEngine ars) {
			this.ars = ars;
		}
		
		public void addGivens(String ... names) {
			for (String name : names) {
				Asset given = getAsset(name); // empty String type for less clutter
				ars.addAssetValue(given, getResult(name));
				givens.add(name);
			}
		}
		
		public String getResult(String name) {
			return name+"-result";
		}
		
		public Asset getAsset(String name) {
			return new Asset(name, "");
		}
		
		public void addResolver(String out, String ... prereqs) {
			addResolver(1, out, prereqs);
		}
		
		public void addResolver(int cost, String out, String ... prereqs) {
			Asset [] prereqAssets = new Asset [prereqs.length];
			
			for (int i=0; i<prereqs.length; i++) prereqAssets[i] = getAsset(prereqs[i]);
			
			ars.addAssetResolver(new AssetResolverMock<Object>(getAsset(out), getResult(out), cost, prereqAssets));
			resolvers.add("(" + out + " <- " + StringUtils.join(prereqs, ",") + ")");
		}
		
		public void logScenarioDescription() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("givens: " + StringUtils.join(givens.iterator(), ", ") + "\n\n");
			sb.append("resolvers:\n----------------------\n");
			if (resolvers == null || resolvers.size() == 0) {
				sb.append("none");
			} else { 
				sb.append(StringUtils.join(resolvers.iterator(), "\n"));
			}
			sb.append("\n");

			LOG.info("Test Scenario:\n\n" + sb.toString());
		}
		
		public void assertSuccess(String toResolve) {
			LOG.info("Testing resolution of " + toResolve);
			try {
				assertEquals(getResult(toResolve), ars.resolveAsset(getAsset(toResolve)));
				LOG.info("Success!");
			} catch (AssetResolutionException e) {
				fail("Should resolve the asset w/o exceptions");
			}
		}

		public void assertException(String toResolve) {
			LOG.info("Testing resolution of " + toResolve);
			try {
				ars.resolveAsset(getAsset(toResolve));
				fail("Should throw AssetResolutionException");
			} catch (AssetResolutionException e) {
				LOG.info("Success! threw " + e);
				// Good, this is what we expect
			}
		}
	}
	
}
