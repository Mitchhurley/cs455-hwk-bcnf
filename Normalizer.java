import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This class provides static methods for performing normalization
 * 
 * @author <YOUR NAME>
 * @version <DATE>
 */
public class Normalizer {

  /**
   * Performs BCNF decomposition
   * 
   * @param rel   A relation (as an attribute set)
   * @param fdset A functional dependency set
   * @return a set of relations (as attribute sets) that are in BCNF
   */
  public static Set<Set<String>> BCNFDecompose(Set<String> rel, FDSet fdset) {
    // TODO - First test if the given relation is already in BCNF with respect to
    // the provided FD set.
	  System.out.println("BCNF Start");
	  Set<Set<String>> superkeys = new HashSet<Set<String>>(findSuperkeys(rel, fdset));
	  if (isBCNF(rel, fdset)) {
		  Set<Set<String>> inBCNF = new HashSet<Set<String>>();
		  inBCNF.add(rel);
		  return inBCNF;
	  }
	  System.out.println("Current schema = " + rel.toString());
	  System.out.println("Current superkeys = " + superkeys.toString());
	  FD violator = findViolation(superkeys, fdset );
	  System.out.println("*** Splitting on " + violator.toString() + " ***");
	  
	  Set<String> left = new HashSet<>(violator.getLeft());
	  left.addAll(violator.getRight());
	  Set<String> right = new HashSet<>(rel);
	  right.removeAll(violator.getRight());
	  right.addAll(violator.getLeft());
	  System.out.println("Left Schema: "+ left.toString());

	  System.out.println("Right Schema: "+ right.toString());
    // TODO - Identify a nontrivial FD that violates BCNF. Split the relation's
    // attributes using that FD, as seen in class.

    // TODO - Redistribute the FDs in the closure of fdset to the two new
    // relations (R_Left and R_Right) as follows:
    //
    // Iterate through closure of the given set of FDs, then union all attributes
    // appearing in the FD, and test if the union is a subset of the R_Left (or
    // R_Right) relation. If so, then the FD gets added to the R_Left's (or R_Right's) FD
    // set. If the union is not a subset of either new relation, then the FD is
    // discarded

    // Repeat the above until all relations are in BCNF
    return null;
  }

  /**
   * Tests whether the given relation is in BCNF. A relation is in BCNF iff the
   * left-hand attribute set of all nontrivial FDs is a super key.
   * 
   * @param rel   A relation (as an attribute set)
   * @param fdset A functional dependency set
   * @return true if the relation is in BCNF with respect to the specified FD set
   */
  public static boolean isBCNF(Set<String> rel, FDSet fdset) {
	  Set<Set<String>> superkeys = new HashSet<Set<String>>(findSuperkeys(rel, fdset));
    for (FD fd:fdset) {
    	if (!fd.isTrivial()) {
    		if (!superkeys.contains(fd.getLeft())){
    			return false;
    		}
    	}
    }
    return true;
  }

  /**
   * This method returns a set of super keys
   * 
   * @param rel   A relation (as an attribute set)
   * @param fdset A functional dependency set
   * @return a set of super keys
   */
  public static Set<Set<String>> findSuperkeys(Set<String> rel, FDSet fdset) {
    // sanity check: are all the attributes in the FD set even in the
    // relation? Throw an IllegalArgumentException if not.
	  Set<String> allAtts = new HashSet<String>();
	  for (FD fd: fdset) {
		  allAtts.addAll(fd.getLeft());
		  allAtts.addAll(fd.getRight());
	  }
	  for (String att: allAtts) {
		  if (!rel.contains(att)) {
			  throw new IllegalArgumentException("FD refers to unknown attributes: " + rel.toString() + " --> " +
				"["+ att + "]"	  );
			  
		  }
	  }
	  //Get all subsets of attributes
	  Set<Set<String>> subs = new HashSet<Set<String>>(FDUtil.powerSet(rel));
	  
	 
	  Set<Set<String>> superkeys = new HashSet<Set<String>>();
	  for (Set<String> subset: subs) {
		  if (calcAttributeClosure(subset, fdset,rel).equals(rel)) {
			  superkeys.add(subset);
		  }
	  }
    return superkeys;
  }
  /**This method determines whether or not a set of attributes can be a superkey by calculating the 
   * closure and comparing it to the full list of attributes
   * 
   * @param attsToTest	A set of attributes to find the closure of
   * @param fdset		The set of FDs to determine attribute closure
   * @param rel			The set of all atributes in the relation
   * @return			true if the attribute closure has all the attributes
   */
  
  public static Set<String> calcAttributeClosure(Set<String> attsToTest, FDSet fdset, Set<String> rel) {
	  Set<String> closure = new HashSet<>(attsToTest);
	  boolean changed = true;
	  while (changed) {
		  //get number of attributes pre iteration for comparison
		  int numAtts = closure.size();
		  //iterate through given FDs
		  for (FD fd: fdset) {

			  if (closure.containsAll(fd.getLeft())) {
				  closure.addAll(fd.getRight());
			  }
		  }
		  if (closure.size() == numAtts) {
			  changed = false;
		  }
	  }
	  return (closure);
  }
  
  
  
  public static FD findViolation(Set<Set<String>> superkeys, FDSet fdset) {
	  for (FD fd:fdset) {
	    	if (!fd.isTrivial()) {
	    		if (!superkeys.contains(fd.getLeft())){
	    			return fd;
	    		}
	    	}
	    }
	  return null;
  }

}