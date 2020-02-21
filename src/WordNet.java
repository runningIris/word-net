import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;

import java.util.ArrayList;

public class WordNet {
    private Digraph G;
    private ArrayList<String> idList;
    private ST<String, Bag<Integer>> st;
    private SAP sap;

    // constructor takes the name of the two input files
    // linearithmic
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("IllegalArgumentException");
        }
        st = new ST<String, Bag<Integer>>();
        idList = new ArrayList<String>();

        int count = 0;
        In synsetsIn = new In(synsets);

        while(synsetsIn.hasNextLine()) {
            String[] split = synsetsIn.readLine().split(",");
            String[] nouns = split[1].split(" ");
            for (int i = 0; i < nouns.length; i++) {
                if (st.contains(nouns[i])) {
                    st.get(nouns[i]).add(Integer.parseInt((split[0])));
                } else {
                    Bag<Integer> bag = new Bag<>();
                    bag.add(Integer.parseInt(split[0]));
                    st.put(nouns[i], bag);
                }
            }
            count++;
            idList.add(split[1]);
        }

        G = new Digraph(count);
        In hypernymsIn = new In(hypernyms);
        boolean[] isNotRoot = new boolean[count];
        int rootNumber = 0;
        while (hypernymsIn.hasNextLine()) {
            String[] split = hypernymsIn.readLine().split(",");
            isNotRoot[Integer.parseInt((split[0]))] = true;
            for (int i = 1; i < split.length; i++) {
                G.addEdge(Integer.parseInt(split[0]), Integer.parseInt(split[i]));
            }
        }

        for (int i = 1; i < count; i++) {
            if (!isNotRoot[i]) rootNumber++;
        }

        DirectedCycle d = new DirectedCycle(G);
        if (rootNumber > 1 || d.hasCycle()) throw new IllegalArgumentException();

        sap = new SAP(G);
    }
    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return st.keys();
    }
    // is the word a WordNet noun?
    // logarithmic
    public boolean isNoun(String noun) {
        return st.contains(noun);
    }
    // distance between nounA and nounB (defined below)
    // linear
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("IllegalArgumentException");
        }
        Bag<Integer> ida = st.get(nounA);
        Bag<Integer> idb = st.get(nounB);

        return sap.length(ida, idb);
    }
    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    // linear
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("IllegalArgumentException");
        }

        Bag<Integer> ida = st.get(nounA);
        Bag<Integer> idb = st.get(nounB);

        int root = sap.ancestor(ida, idb);
        return idList.get(root);
    }
    public static void main(String[] args) {
        WordNet w = new WordNet(args[0], args[1]);
    }
}
