package gov.nasa.pds.android;

import gov.nasa.pds.soap.entities.EntityInfo;
import gov.nasa.pds.soap.entities.Mission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author TCSDESIGNER, TCSDEVELOPER
 * @version 1.0
 */
class Compare {
    public static List<CompareItem> ITEMS = new ArrayList<CompareItem>();

    public static void addMission(Mission mission) {
        if (mission != null && !exists(mission.getId())) {
            ITEMS.add(new CompareItem(mission));
        }
    }

    public static void removeMission(long id) {
        for (Iterator<CompareItem> i = ITEMS.iterator(); i.hasNext();) {
            if (i.next().mission.getId() == id) {
                i.remove();
            }
        }
    }

    public static boolean exists(long id) {
        for (CompareItem item : ITEMS) {
            if (item.getMission().getId() == id) {
                return true;
            }
        }

        return false;
    }

    public static Set<String> findCommonInstruments() {
        // calculate common instruments
        Set<String> commonInstruments = new HashSet<String>();
        if (ITEMS.size() > 1) {
            // add first instruments of first item to list
            Iterator<CompareItem> i = ITEMS.iterator();
            for (EntityInfo instrument : i.next().getInstruments()) {
                commonInstruments.add(instrument.getName());
            }

            // check data with others
            while (i.hasNext()) {
                CompareItem next = i.next();
                for (Iterator<String> j = commonInstruments.iterator(); j.hasNext();) {
                    String commonInstrument = j.next();

                    // find instrument in next item
                    boolean present = true;
                    for (EntityInfo entityInfo : next.getInstruments()) {
                        if (!entityInfo.getName().equalsIgnoreCase(commonInstrument)) {
                            present = false;
                            break;
                        }
                    }

                    // remove instrument if it is not present in next
                    if (!present) {
                        j.remove();
                    }
                }
            }
        }
        return commonInstruments;
    }

    static class CompareItem {
        private final Mission mission;
        private final List<EntityInfo> instruments = new ArrayList<EntityInfo>();

        private CompareItem(Mission mission) {
            this.mission = mission;
        }

        public Mission getMission() {
            return mission;
        }

        public List<EntityInfo> getInstruments() {
            return instruments;
        }
    }
}
