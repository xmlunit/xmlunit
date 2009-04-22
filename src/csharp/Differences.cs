namespace XmlUnit {
    public class Differences {
        private Differences() { }
        
        public static bool isMajorDifference(DifferenceType differenceType) {
            switch (differenceType) {
                case DifferenceType.ATTR_SEQUENCE_ID:
                    return false;
                case DifferenceType.HAS_XML_DECLARATION_PREFIX_ID:
                    return false;
                default:
                    return true;
            }
        }
        
    }
}
