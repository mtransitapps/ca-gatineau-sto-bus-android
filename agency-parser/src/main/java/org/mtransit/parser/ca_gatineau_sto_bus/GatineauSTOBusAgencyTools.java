package org.mtransit.parser.ca_gatineau_sto_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.commons.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mtransit.commons.RegexUtils.DIGITS;
import static org.mtransit.commons.StringUtils.EMPTY;

// https://www.sto.ca/en/business/developer-space-open-data/
// https://www.sto.ca/affaires/espace-developpeurs-donnees-ouvertes/
public class GatineauSTOBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new GatineauSTOBusAgencyTools().start(args);
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "STO";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true; // used for GTFS-RT
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.toLowerCaseUpperCaseWords(Locale.FRENCH, routeLongName, getIgnoredWords());
		routeLongName = CEGEP_GABRIELLE_ROY_.matcher(routeLongName).replaceAll(CEGEP_GABRIELLE_ROY_REPLACEMENT);
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean mergeRouteLongName(@NotNull MRoute mRoute, @NotNull MRoute mRouteToMerge) {
		if (mRoute.simpleMergeLongName(mRouteToMerge)) {
			return super.mergeRouteLongName(mRoute, mRouteToMerge);
		}
		List<String> routeLongNames = Arrays.asList(mRoute.getLongName(), mRouteToMerge.getLongName());
		if (mRoute.getId() == 33L) {
			if (Arrays.asList( //
					"Station Cité" + _SLASH_ + "G-Roy" + _SLASH_ + "Ottawa", //
					"Station Cité" + _SLASH_ + "Cegep G-Roy" + _SLASH_ + "Ottawa", //
					"Station De La Cité" + _SLASH_ + "Cegep Gabrielle-Roy" + _SLASH_ + "Ottawa" //
			).containsAll(routeLongNames)) {
				mRoute.setLongName("Station De La Cité" + _SLASH_ + "Cegep Gabrielle-Roy" + _SLASH_ + "Ottawa");
				return true;
			}
		} else if (mRoute.getId() == 37L) {
			if (Arrays.asList( //
					"Cegep G-Roy", //
					"Cegep Gabrielle-Roy" //
			).containsAll(routeLongNames)) {
				mRoute.setLongName("Cegep Gabrielle-Roy");
				return true;
			}
			if (Arrays.asList( //
					"Cegep G-Roy", //
					"Cegep Gabrielle-Roy", //
					"Cegep Gab-Roy" + _SLASH_ + "St-Joseph" //
			).containsAll(routeLongNames)) {
				mRoute.setLongName("Cegep Gab-Roy" + _SLASH_ + "St-Joseph");
				return true;
			}
		} else if (mRoute.getId() == 66L) {
			if (Arrays.asList( //
					"Mont-Luc" + _SLASH_ + "Dubarry", //
					"Montluc Dubarry" //
			).containsAll(routeLongNames)) {
				mRoute.setLongName("Mont-Luc" + _SLASH_ + "Dubarry");
				return true;
			}
		} else if (mRoute.getId() == 88L) {
			if (Arrays.asList( //
					"Station Labrosse" + _SLASH_ + "Cheval-Blanc", //
					"Station Labrosse-Cheval Blanc" //
			).containsAll(routeLongNames)) {
				mRoute.setLongName("Station Labrosse" + _SLASH_ + "Cheval-Blanc");
				return true;
			}
		}
		throw new MTLog.Fatal("Unexpected routes to merge: %s & %s!", mRoute, mRouteToMerge);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	// private static final String AGENCY_COLOR_GREEN = "33A949"; // GREEN PANTONE 360 / 361 (90%) (from Corporate Logo Usage PDF)
	private static final String AGENCY_COLOR_BLUE = "007F89"; // BLUE PANTONE 7474 (from Corporate Logo Usage PDF)

	private static final String AGENCY_COLOR = AGENCY_COLOR_BLUE;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String REGULAR_COLOR = "231F20"; // "000000"; // BLACK (from PDF)
	private static final String PEAK_COLOR = "9B0078"; // VIOLET (from PDF)
	private static final String RB100_COLOR = "0067AC"; // BLUE (from PDF)
	private static final String RB200_COLOR = "DA002E"; // RED (from PDF)
	private static final String SCHOOL_BUS_COLOR = "FFD800"; // YELLOW (from Wikipedia)

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
			final int rsn = Integer.parseInt(gRoute.getRouteShortName());
			final String rln = gRoute.getRouteLongNameOrDefault();
			if (rsn > 100 && SCHOOL_ROUTE_LONG_NAME_.matcher(rln).find()) {
				return SCHOOL_BUS_COLOR;
			}
			switch (rsn) {
			// @formatter:off
			case 11: return PEAK_COLOR;
			case 15: return PEAK_COLOR;
			case 17: return PEAK_COLOR;
			case 18: return REGULAR_COLOR;
			case 20: return PEAK_COLOR;
			case 21: return REGULAR_COLOR;
			case 22: return PEAK_COLOR;
			case 23: return PEAK_COLOR;
			case 24: return PEAK_COLOR;
			case 25: return PEAK_COLOR;
			case 26: return PEAK_COLOR;
			case 27: return PEAK_COLOR;
			case 28: return PEAK_COLOR;
			case 29: return PEAK_COLOR;
			case 31: return REGULAR_COLOR; // OCCASIONAL_COLOR;
			case 32: return PEAK_COLOR;
			case 33: return REGULAR_COLOR; // OCCASIONAL_COLOR;
			case 34: return REGULAR_COLOR;
			case 35: return REGULAR_COLOR; // OCCASIONAL_COLOR;
			case 36: return REGULAR_COLOR; // OCCASIONAL_COLOR;
			case 37: return REGULAR_COLOR; // OCCASIONAL_COLOR;
			case 38: return REGULAR_COLOR; // OCCASIONAL_COLOR;
			case 39: return REGULAR_COLOR;
			case 40: return PEAK_COLOR;
			case 41: return PEAK_COLOR;
			case 44: return PEAK_COLOR;
			case 45: return PEAK_COLOR;
			case 46: return PEAK_COLOR;
			case 47: return PEAK_COLOR;
			case 48: return PEAK_COLOR;
			case 49: return REGULAR_COLOR;
			case 50: return PEAK_COLOR;
			case 51: return REGULAR_COLOR;
			case 52: return REGULAR_COLOR;
			case 53: return REGULAR_COLOR;
			case 54: return PEAK_COLOR;
			case 55: return REGULAR_COLOR;
			case 56: return REGULAR_COLOR;
			case 57: return REGULAR_COLOR;
			case 58: return PEAK_COLOR;
			case 59: return REGULAR_COLOR; // OCCASIONAL_COLOR;
			case 60: return PEAK_COLOR;
			case 61: return PEAK_COLOR;
			case 62: return REGULAR_COLOR;
			case 63: return REGULAR_COLOR;
			case 64: return REGULAR_COLOR;
			case 65: return REGULAR_COLOR;
			case 66: return REGULAR_COLOR;
			case 67: return PEAK_COLOR;
			case 68: return REGULAR_COLOR; // RAPIBUS_COLOR
			case 69: return REGULAR_COLOR;
			case 71: return REGULAR_COLOR;
			case 72: return null; // TODO ?
			case 73: return REGULAR_COLOR;
			case 74: return PEAK_COLOR;
			case 75: return REGULAR_COLOR;
			case 76: return REGULAR_COLOR;
			case 77: return REGULAR_COLOR;
			case 78: return REGULAR_COLOR;
			case 79: return REGULAR_COLOR;
			case 85: return PEAK_COLOR;
			case 87: return PEAK_COLOR;
			case 88: return PEAK_COLOR;
			case 93: return PEAK_COLOR; // RAPIBUS_COLOR
			case 95: return PEAK_COLOR; // RAPIBUS_COLOR
			case 94: return PEAK_COLOR;
			case 97: return REGULAR_COLOR;
			case 98: return PEAK_COLOR;
			case 100: return RB100_COLOR; // RAPIBUS_COLOR
			case 134: return REGULAR_COLOR;
			case 200: return RB200_COLOR; // RAPIBUS_COLOR
			case 300: return REGULAR_COLOR; // RAPIBUS_COLOR
			case 400: return REGULAR_COLOR; // RAPIBUS_COLOR
			case 425: return null; // TODO ?
			case 432: return null; // TODO ?
			case 472: return null; // TODO ?
			case 500: return REGULAR_COLOR; // RAPIBUS_COLOR
			case 534: return null; // TODO ?
			case 549: return null; // TODO ?
			case 550: return null; // TODO ?
			case 566: return null; // TODO ?
			case 571: return null; // TODO ?
			case 576: return null; // TODO ?
			case 597: return null; // TODO ?
			case 726: return null; // TODO ?
			case 734: return null; // TODO ?
			case 747: return null; // TODO ?
			case 750: return null; // TODO ?
			case 800: return REGULAR_COLOR; // RAPIBUS_COLOR
			case 804: return null; // TODO ?
			case 805: return null; // TODO ?
			case 807: return null; // TODO ?
			case 810: return REGULAR_COLOR; // RAPIBUS_COLOR
			case 811: return null; // TODO ?
			case 813: return null; // TODO ?
			case 824: return null; // TODO ?
			case 825: return null; // TODO ?
			case 827: return null; // TODO ?
			case 831: return null; // TODO ?
			case 833: return null; // TODO ?
			case 834: return null; // TODO ?
			case 837: return null; // TODO ?
			case 859: return null; // TODO ?
			case 867: return null; // TODO ?
			case 870: return PEAK_COLOR; // RAPIBUS_COLOR // TODO ??
			case 873: return null; // TODO ?
			case 874: return null; // TODO ?
			case 876: return null; // TODO ?
			case 878: return null; // TODO ?
			case 901: return null; // TODO ?
			case 902: return null; // TODO ?
			case 904: return null; // TODO ?
			case 929: return null; // TODO ?
			case 931: return null; // TODO ?
			case 932: return null; // TODO ?
			case 933: return null; // TODO ?
			case 934: return null; // TODO ?
			case 935: return null; // TODO ?
			case 937: return null; // TODO ?
			case 949: return null; // TODO ?
			case 950: return null; // TODO ?
			case 990: return null; // TODO ?
			// @formatter:on
			}
			throw new MTLog.Fatal("Unexpected route color %s!", gRoute.toStringPlus());
	}

	private static final Pattern SCHOOL_ROUTE_LONG_NAME_ = Pattern.compile("("
			+ "cgp|c([eé])gep"
			+ "|"
			+ "([eé])cole"
			+ "|"
			+ "gabrielle-roy|g-roy"
			+ "|"
			+ "de l'([iî])le"
			+ "|"
			+ "mont-bleu"
			+ "|"
			+ "(grande|gr)([ \\-])rivi([èe])re|esgr"
			+ "|"
			+ "stjo|st-joseph"
			+ "|"
			+ "st-alexandre"
			+ "|"
			+ "(nv|nouvelles) fronti([èe])res"
			+ "|"
			+ "hormisdas-gamelin"
			+ "|"
			+ "cado" // Centre Académique De L'Outaouais
			+ ")", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final String _SLASH_ = " / ";

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@Override
	public boolean allowNonDescriptiveHeadSigns(long routeId) {
		if (routeId == 79L) {
			return true; // 2023-08-15: 2 directions with 2 head-signs merged into 2 directions with same head-signs
		}
		return super.allowNonDescriptiveHeadSigns(routeId);
	}

	private static final String MUSEE_CANADIEN_HISTOIRE_SHORT = "Musée de l'Histoire";
	private static final Pattern MUSEE_CANADIEN_HISTOIRE_ = Pattern.compile("((^|\\W)(mus[e|é]e canadien de l'histoire)(\\W|$))",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final String MUSEE_CANADIEN_HISTOIRE_REPLACEMENT = "$2" + MUSEE_CANADIEN_HISTOIRE_SHORT + "$4";

	private static final String STATION_ = ""; // "Ston ";
	private static final Pattern CLEAN_STATION = Pattern.compile("((^|\\W)(station|ston|sta)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String CLEAN_STATION_REPLACEMENT = "$2" + STATION_ + "$4";

	private static final String CEGEP_GABRIELLE_ROY_SHORT = "Cgp GRoy";
	private static final Pattern CEGEP_GABRIELLE_ROY_ = Pattern.compile("((^|\\W)(" //
			+ "c[é|e]gep gabrielle-roy" //
			+ "|" //
			+ "cegep gab\\.roy" //
			+ "|" //
			+ "cegep gab-roy"//
			+ "|" //
			+ "c[é|e]gep groy" //
			+ "|" //
			+ "c[é|e]gep g-roy" //
			+ "|" //
			+ "cegep g-roy" //
			+ "|" //
			+ "c[é|e]gep g\\.roy" //
			+ "|" //
			+ "cgp gabrielle-r" //
			+ "|" //
			+ "cgp groy" //
			+ "|" //
			+ "cgp g-roy" //
			+ "|" //
			+ "g-roy" //
			+ ")(\\W|$))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final String CEGEP_GABRIELLE_ROY_REPLACEMENT = "$2" + CEGEP_GABRIELLE_ROY_SHORT + "$4";

	private static final Pattern ECOLE_X_ = CleanUtils.cleanWordFR("((école secondaire|ecole secondaire|école sec|ecole sec|école|ecole|e)(\\.|\\s){1,2}(\\w+))");
	private static final String ECOLE_X_REPLACEMENT = CleanUtils.cleanWordsReplacement("É $7");

	private static final String LAVIGNE = "Lavigne";
	private static final String JARDINS_LAVIGNE_SHORT = "J" + LAVIGNE;
	private static final Pattern JARDINS_LAVIGNE_ = Pattern.compile("((^|\\W)(jardins lavigne|jlavigne)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String JARDINS_LAVIGNE_REPLACEMENT = "$2" + JARDINS_LAVIGNE_SHORT + "$4";

	private static final String GALERIES_AYLMER_SHORT = "Gal.Aylmer";
	private static final Pattern GALERIES_AYLMER_ = Pattern.compile("((^|\\W)(galeries aylmer|gal\\.aylmer)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String GALERIES_AYLMER_REPLACEMENT = "$2" + GALERIES_AYLMER_SHORT + "$4";

	private static final String COLLEGE_SHORT = "Col";
	private static final Pattern COLLEGE_ = Pattern.compile("((^|\\W)(college|collège|collége)(\\W|$))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
	private static final String COLLEGE_REPLACEMENT = "$2" + COLLEGE_SHORT + "$4";

	private static final String COLLEGE_NOUVELLES_FRONTIERES_SHORT = COLLEGE_SHORT + " NF";
	private static final Pattern COLLEGE_NOUVELLES_FRONTIERES_ = Pattern.compile("((^|\\W)(col nf|col nouvelles frontieres|col nouvelles frontières)(\\W|$))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final String COLLEGE_NOUVELLES_FRONTIERES_REPLACEMENT = "$2" + COLLEGE_NOUVELLES_FRONTIERES_SHORT + "$4";

	private static final String COLLEGE_SAINT_JOSEPH_SHORT = COLLEGE_SHORT + " St-Jo";
	private static final Pattern COLLEGE_SAINT_JOSEPH_ = Pattern.compile("((^|\\W)(c stjoseph)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String COLLEGE_SAINT_JOSEPH_REPLACEMENT = "$2" + COLLEGE_SAINT_JOSEPH_SHORT + "$4";

	private static final Pattern GRANDE_RIVIERE_ = CleanUtils.cleanWordsFR("grande-rivière", "gr-rivière");
	private static final String GRANDE_RIVIERE_REPLACEMENT = CleanUtils.cleanWordsReplacement("Gr-Riv");

	private static final String COLLEGE_SAINT_ALEXANDRE_SHORT = COLLEGE_SHORT + " St-Alex";
	private static final Pattern COLLEGE_SAINT_ALEXANDRE_ = Pattern.compile("((^|\\W)(col stalex)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String COLLEGE_SAINT_ALEXANDRE_REPLACEMENT = "$2" + COLLEGE_SAINT_ALEXANDRE_SHORT + "$4";

	private static final String MASSON_ANGERS = "M-Angers";
	private static final Pattern MASSON_ANGERS_ = Pattern.compile("((^|\\W)(masson-angers)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String MASSON_ANGERS_REPLACEMENT = "$2" + MASSON_ANGERS + "$4";

	private static final String ALLUMETTIERES_SHORT = "Allum";
	private static final Pattern ALLUMETTIERES_ = Pattern.compile("((^|\\W)(des allumetti[è|e]res|allumetti[è|e]res|allum)(\\W|$))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final String ALLUMETTIERES_REPLACEMENT = "$2" + ALLUMETTIERES_SHORT + "$4";

	private static final String PLACE_D_ACCUEIL = "Pl.Accueil"; // "Place d'Accueil";
	private static final Pattern PLACE_D_ACCUEIL_ = Pattern.compile("((^|\\W)(place d'accueil|pl\\.accueil)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String PLACE_D_ACCUEIL_REPLACEMENT = "$2" + PLACE_D_ACCUEIL + "$4";

	private static final String COTES_DES_NEIGES = "Côtes-Des-Neiges";
	private static final Pattern COTES_DES_NEIGES_ = Pattern.compile("((^|\\W)(c[ô|o]tes-des-neiges|coteneige)(\\W|$))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final String COTES_DES_NEIGES_REPLACEMENT = "$2" + COTES_DES_NEIGES + "$4";

	private static final String P_O_B_SHORT = "P-O-B";
	private static final Pattern P_O_B = Pattern.compile("((^|\\W)(pob|p-o-b|parc o bus|parc-o-bus)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String P_O_B_REPLACEMENT = "$2" + P_O_B_SHORT + "$4";

	private static final Pattern PRE_TUNNEY_ = Pattern.compile("((^|\\W)(pr[e|é] tunney)(\\W|$))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final String PRE_TUNNEY_REPLACEMENT = "$2" + "Pré-Tunney" + "$4";

	private static final Pattern MONT_BLEU_ = CleanUtils.cleanWordFR("mont-bleu");
	private static final String MONT_BLEU_REPLACEMENT = CleanUtils.cleanWordsReplacement("MontBleu");

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = CleanUtils.toLowerCaseUpperCaseWords(Locale.FRENCH, tripHeadsign, getIgnoredWords());
		tripHeadsign = CLEAN_STATION.matcher(tripHeadsign).replaceAll(CLEAN_STATION_REPLACEMENT);
		tripHeadsign = CEGEP_GABRIELLE_ROY_.matcher(tripHeadsign).replaceAll(CEGEP_GABRIELLE_ROY_REPLACEMENT);
		tripHeadsign = COLLEGE_.matcher(tripHeadsign).replaceAll(COLLEGE_REPLACEMENT);
		tripHeadsign = COLLEGE_NOUVELLES_FRONTIERES_.matcher(tripHeadsign).replaceAll(COLLEGE_NOUVELLES_FRONTIERES_REPLACEMENT);
		tripHeadsign = COLLEGE_SAINT_JOSEPH_.matcher(tripHeadsign).replaceAll(COLLEGE_SAINT_JOSEPH_REPLACEMENT);
		tripHeadsign = COLLEGE_SAINT_ALEXANDRE_.matcher(tripHeadsign).replaceAll(COLLEGE_SAINT_ALEXANDRE_REPLACEMENT);
		tripHeadsign = ECOLE_X_.matcher(tripHeadsign).replaceAll(ECOLE_X_REPLACEMENT); // E instead of ES
		tripHeadsign = GRANDE_RIVIERE_.matcher(tripHeadsign).replaceAll(GRANDE_RIVIERE_REPLACEMENT);
		tripHeadsign = GALERIES_AYLMER_.matcher(tripHeadsign).replaceAll(GALERIES_AYLMER_REPLACEMENT);
		tripHeadsign = JARDINS_LAVIGNE_.matcher(tripHeadsign).replaceAll(JARDINS_LAVIGNE_REPLACEMENT);
		tripHeadsign = ALLUMETTIERES_.matcher(tripHeadsign).replaceAll(ALLUMETTIERES_REPLACEMENT);
		tripHeadsign = COTES_DES_NEIGES_.matcher(tripHeadsign).replaceAll(COTES_DES_NEIGES_REPLACEMENT);
		tripHeadsign = P_O_B.matcher(tripHeadsign).replaceAll(P_O_B_REPLACEMENT);
		tripHeadsign = PLACE_D_ACCUEIL_.matcher(tripHeadsign).replaceAll(PLACE_D_ACCUEIL_REPLACEMENT);
		tripHeadsign = PRE_TUNNEY_.matcher(tripHeadsign).replaceAll(PRE_TUNNEY_REPLACEMENT);
		tripHeadsign = MUSEE_CANADIEN_HISTOIRE_.matcher(tripHeadsign).replaceAll(MUSEE_CANADIEN_HISTOIRE_REPLACEMENT);
		tripHeadsign = MASSON_ANGERS_.matcher(tripHeadsign).replaceAll(MASSON_ANGERS_REPLACEMENT);
		tripHeadsign = MONT_BLEU_.matcher(tripHeadsign).replaceAll(MONT_BLEU_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_ET.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_ET_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	private String[] getIgnoredWords() {
		return new String[]{
				"AM", "PM",
				"GD", "STO",
		};
	}

	private static final Pattern STARTS_ENDS_WITH_ARRIVAL_DEPARTURE = Pattern.compile("(^"
			+ "(arriv[e|é]e|d[e|é]part)"
			+ "|"
			+ "[/\\-]?\\s*(arr[e|ê]t d'arriv[e|é]e|arriv[e|é]e|d[e|é]part)"
			+ "|"
			+ "( - temps d'attente)"
			+ "$)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	private static final Pattern CLEAN_ARRET_DE_COURTOISIE = Pattern.compile("((arr[e|ê]t de courtoisie\\s*)(.*))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final String CLEAN_ARRET_DE_COURTOISIE_REPLACEMENT = "$3 (Arrêt de Courtoisie)";

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(Locale.FRENCH, gStopName, getIgnoredWords());
		gStopName = CleanUtils.cleanBounds(Locale.FRENCH, gStopName);
		gStopName = STARTS_ENDS_WITH_ARRIVAL_DEPARTURE.matcher(gStopName).replaceAll(EMPTY);
		gStopName = CLEAN_ARRET_DE_COURTOISIE.matcher(gStopName).replaceAll(CLEAN_ARRET_DE_COURTOISIE_REPLACEMENT);
		gStopName = P_O_B.matcher(gStopName).replaceAll(P_O_B_REPLACEMENT);
		gStopName = ECOLE_X_.matcher(gStopName).replaceAll(ECOLE_X_REPLACEMENT); // E instead of ES
		gStopName = CleanUtils.SAINT.matcher(gStopName).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_ET.matcher(gStopName).replaceAll(CleanUtils.CLEAN_ET_REPLACEMENT);
		gStopName = CleanUtils.cleanSlashes(gStopName);
		gStopName = CleanUtils.cleanStreetTypesFRCA(gStopName);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		if (StringUtils.isEmpty(gStop.getStopCode())) {
			//noinspection deprecation
			return gStop.getStopId(); // use stop ID as stop code
		}
		return super.getStopCode(gStop);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		//noinspection deprecation
		final String stopId = gStop.getStopId();
		if (!CharUtils.isDigitsOnly(stopId)) {
			final Matcher matcher = DIGITS.matcher(stopId);
			if (matcher.find()) {
				final int digits = Integer.parseInt(matcher.group());
				if (stopId.toLowerCase(Locale.FRENCH).endsWith("a")) {
					return 100_000 + digits;
				}
			}
			throw new MTLog.Fatal("Unexpected stop ID for %s!", gStop);
		}
		return super.getStopId(gStop); // used for GTFS-RT
	}
}
