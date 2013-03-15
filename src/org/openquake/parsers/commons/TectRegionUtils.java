package org.openquake.parsers.commons;

import java.util.Map;

import org.opensha.sha.util.TectonicRegionType;


/**
 * @author laurentiudanciu
 *
 */
public class TectRegionUtils {

	/**
	 * Get tectonic region type (from SHARE regionalization to OpenSHA-GMPE
	 * regionalization)
	 */
	/**
	 * @param tecRegType
	 * @return
	 */
	public static TectonicRegionType getTectonicRegionType(String tecRegType) {

		if (tecRegType.equalsIgnoreCase(ShapeFileConstants.ACTIVE_SHALLOW_CRUST)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		} else if (tecRegType.equalsIgnoreCase(ShapeFileConstants.OCEANIC_RIDGE)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		} else if (tecRegType.equalsIgnoreCase(ShapeFileConstants.AZORES_GIBRALTAR)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		} else if (tecRegType.equalsIgnoreCase(ShapeFileConstants.OCEANIC_CRUST)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		} else if (tecRegType.equalsIgnoreCase(ShapeFileConstants.STABLE_CONTINENTAL_EXT)) {
			return TectonicRegionType.STABLE_SHALLOW;
		} else if (tecRegType.equalsIgnoreCase(ShapeFileConstants.STABLE_CONTINENTAL_SHIELD)){
			return TectonicRegionType.STABLE_SHALLOW;
		} else if (tecRegType.equalsIgnoreCase(ShapeFileConstants.STABLE_CONTINENTAL_NO_EXT)) {
			return TectonicRegionType.STABLE_SHALLOW;
		} else
			throw new RuntimeException("tectonic region type not recognized");
	}
	
	/**
	 * Get tectonic region type (from SHARE regionalization to OpenSHA-GMPE
	 * regionalization)
	 */
	/**
	 * @param srcRecord
	 * @return
	 */
	public static TectonicRegionType getTectonicRegionTypeAsModel(Map<String, Object> srcRecord) {

		String tecRegType = (String)srcRecord.get(ShapeFileConstants.tectonicRegionType);
		if (tecRegType
				.equalsIgnoreCase(ShapeFileConstants.ACTIVE_SHALLOW_CRUST)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		} else if (tecRegType
				.equalsIgnoreCase(ShapeFileConstants.OCEANIC_RIDGE)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		} else if (tecRegType
				.equalsIgnoreCase(ShapeFileConstants.AZORES_GIBRALTAR)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		} else if (tecRegType
				.equalsIgnoreCase(ShapeFileConstants.OCEANIC_CRUST)) {
			return TectonicRegionType.ACTIVE_SHALLOW;
		} else if (tecRegType
				.equalsIgnoreCase(ShapeFileConstants.STABLE_CONTINENTAL_EXT)) {
			return TectonicRegionType.STABLE_SHALLOW;
		} else if (tecRegType
				.equalsIgnoreCase(ShapeFileConstants.STABLE_CONTINENTAL_SHIELD)){
			return TectonicRegionType.STABLE_SHALLOW;
		} else if (tecRegType
				.equalsIgnoreCase(ShapeFileConstants.STABLE_CONTINENTAL_NO_EXT)) {
			return TectonicRegionType.STABLE_SHALLOW;
		} else if (tecRegType
				.equalsIgnoreCase(ShapeFileConstants.SUBDUCTION_INSLAB)) {
			return TectonicRegionType.SUBDUCTION_SLAB;
		} else if (tecRegType
				.equalsIgnoreCase(ShapeFileConstants.SUBDUCTION_INTERFACE)) {
			return TectonicRegionType.SUBDUCTION_INTERFACE;

		} else
			throw new RuntimeException("tectonic region type not recognized");
	}
	

}
