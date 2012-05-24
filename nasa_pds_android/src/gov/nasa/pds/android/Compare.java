/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */
package gov.nasa.pds.android;

import gov.nasa.pds.soap.entities.EntityInfo;
import gov.nasa.pds.soap.entities.Mission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class represent data that will be compared.
 *
 * @author 7realm
 * @version 1.0
 */
class Compare {
    /** List of current items to be compared. */
    public static List<CompareItem> ITEMS = new ArrayList<CompareItem>();

    /**
     * Add mission to compare.
     *
     * @param mission mission to compare
     */
    public static void addMission(Mission mission) {
        if (mission != null && !exists(mission.getId())) {
            ITEMS.add(new CompareItem(mission));
        }
    }

    /**
     * Remove mission from compare by id.
     *
     * @param id the id of mission to remove
     */
    public static void removeMission(long id) {
        for (Iterator<CompareItem> i = ITEMS.iterator(); i.hasNext();) {
            if (i.next().mission.getId() == id) {
                i.remove();
            }
        }
    }

    /**
     * Check if mission with give id exists in compare list.
     *
     * @param id the id of mission to check
     * @return true if mission exists, false otherwise
     */
    public static boolean exists(long id) {
        for (CompareItem item : ITEMS) {
            if (item.getMission().getId() == id) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds common instruments for all missions in compare list.
     *
     * @return the set of common instruments names
     */
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

    /**
     * The compare item data.
     *
     * @author 7realm
     * @version 1.0
     */
    static class CompareItem {
        private final Mission mission;
        private final List<EntityInfo> instruments = new ArrayList<EntityInfo>();

        private CompareItem(Mission mission) {
            this.mission = mission;
        }

        /**
         * Get stored mission.
         *
         * @return the mission stored in this item
         */
        public Mission getMission() {
            return mission;
        }

        /**
         * The list of instruments, stored in this item.
         *
         * @return the list of instruments
         */
        public List<EntityInfo> getInstruments() {
            return instruments;
        }
    }
}
