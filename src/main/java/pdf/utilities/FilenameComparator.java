package pdf.utilities;

import java.io.File;
import java.math.BigInteger;
import java.util.Comparator;

public final class FilenameComparator implements Comparator<File> {

    private static final class StringParser {
        private final String str;
        private final int len;
        private int pos = 0;
        private int segments = 0;

        public StringParser(String str) {
            this.str = str;
            this.len = str.length();
            // Original NUMBERS.split("") returns an array with one empty string.
            if (this.len == 0) {
                segments++;
            }
        }

        public String nextSegment() {
            if (len == 0 && pos == 0) {
                pos = 1;
                return "";
            }
            if (pos >= len) return null;
            int start = pos;
            boolean isDigit = (str.charAt(start) >= '0' && str.charAt(start) <= '9');
            pos++;
            while (pos < len && (str.charAt(pos) >= '0' && str.charAt(pos) <= '9') == isDigit) {
                pos++;
            }
            segments++;
            return str.substring(start, pos);
        }

        public int getRemainingSegments() {
            while (pos < len) {
                int start = pos;
                boolean isDigit = (str.charAt(start) >= '0' && str.charAt(start) <= '9');
                pos++;
                while (pos < len && (str.charAt(pos) >= '0' && str.charAt(pos) <= '9') == isDigit) {
                    pos++;
                }
                segments++;
            }
            return segments;
        }
    }

    @Override
    public int compare(File o1, File o2) {
        // Optional "NULLS LAST" semantics:
        if (o1 == null || o2 == null) {
            if (o1 != null) return 1;
            return (o2 == null) ? 0 : -1;
        }

        StringParser p1 = new StringParser(o1.getName());
        StringParser p2 = new StringParser(o2.getName());

        while (true) {
            String s1 = p1.nextSegment();
            String s2 = p2.nextSegment();

            if (s1 == null || s2 == null) {
                if (s1 != null) {
                    return p1.getRemainingSegments() - p2.segments;
                } else if (s2 != null) {
                    return p1.segments - p2.getRemainingSegments();
                } else {
                    return p1.segments - p2.segments;
                }
            }

            char c1 = s1.isEmpty() ? 0 : s1.charAt(0);
            char c2 = s2.isEmpty() ? 0 : s2.charAt(0);
            int cmp = 0;

            // If both segments start with a digit, sort them numerically using
            // BigInteger to stay safe
            if (c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9') {
                cmp = new BigInteger(s1).compareTo(new BigInteger(s2));
            }

            // If we haven't sorted numerically before, or if numeric sorting yielded
            // equality (e.g 007 and 7) then sort lexicographically
            if (cmp == 0) {
                cmp = s1.compareTo(s2);
            }

            // Abort once some prefix has unequal ordering
            if (cmp != 0) {
                return cmp;
            }
        }
    }
}
