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
    //First test if the given relation is already in BCNF with respect to
    // the provided FD set.
	  Set<Set<String>> superkeys = new HashSet<Set<String>>(findSuperkeys(rel, fdset));
	  if (isBCNF(rel, fdset)) {
		  Set<Set<String>> inBCNF = new HashSet<Set<String>>();
		  inBCNF.add(rel);
		  return inBCNF;
	  }
	  //If not find Violationg FD
	  System.out.println("BCNF Start");
	  System.out.println("Current schema = " + rel.toString());
	  System.out.println("Current superkeys = " + superkeys.toString());
	  FD violator = findViolation(superkeys, fdset );
	  System.out.println("*** Splitting on " + violator.toString() + " ***");
	  //Split based on violating FD
	  Set<String> left = new HashSet<>(violator.getLeft());
	  left.addAll(violator.getRight());
	  Set<String> right = new HashSet<>(rel);
	  right.removeAll(violator.getRight());
	  right.addAll(violator.getLeft());
	  //assign all fds to either left or right
	  FDSet leftFDs = new FDSet();
	  FDSet rightFDs = new FDSet();
	 
	  FDSet closure = new FDSet(FDUtil.fdSetClosure(new FDSet(fdset)));
	  for (FD fd: closure) {
		  Set<String> atts = new HashSet<>(fd.getLeft());
		  atts.addAll(fd.getRight());
		  if (left.containsAll(atts)) {
			  leftFDs.add(fd);
		  }else if (right.containsAll(atts)) {
			  rightFDs.add(fd);
		  }
	  }
	  System.out.println("Left Schema: "+ left.toString());
	  System.out.println("Left Schema's superkeys = " + findSuperkeys(left, leftFDs));
	  System.out.println("Right Schema: "+ right.toString());
	  System.out.println("Right Schema's superkeys = " + findSuperkeys(right, rightFDs));
	  //Recurse on left and right
	  Set<Set<String>> schema = new HashSet<Set<String>>();
	  schema.addAll(BCNFDecompose(left, leftFDs));
	  schema.addAll(BCNFDecompose(right, rightFDs));
	  System.out.println("BCNF End");
    return schema;
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
   * @return			set of attribute closure
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
  
  
  /**Method that finds the functional dependency that prevents the relation from being in bcnf
   * 
   * @param superkeys	the superkeys of the set
   * @param fdset		the set of FDs to test
   * @return 			the FD that violates BCNF
   */
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