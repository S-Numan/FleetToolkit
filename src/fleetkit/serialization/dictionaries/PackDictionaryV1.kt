package fleetkit.serialization.dictionaries

import fleetkit.serialization.core.ByteBuffers

object PackDictionaryV1 {
    /** Builds a dictionary from strings ordered least common -> most common. Max 32 KB. */
    fun build(strings: List<String>): ByteArray {
        val out = ByteBuffers.Out()
        for (s in strings) out.writeUTF(s)
        val bytes = out.toByteArray()
        // zlib only uses the last 32 KB, so drop the front (least common) if too large
        return if (bytes.size > 32768) bytes.copyOfRange(bytes.size - 32768, bytes.size) else bytes
    }

    // Never edit any of the below or it'll break all past compression.

    // Person names: least likely first. Small name pools last since each name there is likelier to appear.
    val personNames: List<String> = listOf(
        // first names: old english / modern
        "Thomas", "Jackson", "Martin", "Lewis", "Lee", "Allen", "Scott", "Nelson", "Stewart", "Morgan", "Howard",
        "Ward", "James", "Kelly", "Bennett", "Alexander", "Jordan", "Henry", "Mason", "Kennedy", "Warren", "Dixon",
        "Gordon", "Grant", "Rose", "Spencer", "Pierce", "Arnold", "Ray", "Bradley", "Lane", "Riley", "Lawrence",
        "Elliott", "Ryan", "Oliver", "George", "Kim", "Gilbert", "Day", "Douglas", "May", "Terry", "Wade", "Curtis",
        "Neal", "Gregory", "Leonard", "Dennis", "Todd", "Blair", "Reese", "Joseph", "Francis", "Conner", "Norman",
        "Quinn", "Blake", "Paul", "Lindsey", "Tyler", "Sherman", "Gill", "Simon", "Roy", "Logan", "Patrick",
        "Norton", "Lloyd", "Carson", "Lamb", "Casey", "Clarke", "Cain", "Bryan", "Allison", "Richard", "Chase",
        "Roman", "Randall", "Charles", "Bruce", "Cameron", "Christian", "Wyatt", "Keith", "Keir", "Rich", "John",
        "Moses", "Tanner", "Walter", "Nixon", "Leon", "Lara", "Shannon", "Ashley", "Frost", "William", "Solomon",
        "Dudley", "Nolan", "Kane", "Joyce", "Haley", "Valentine", "Buck", "Moon", "Barry", "Avery", "Whitney",
        "Terrell", "Spence", "Case", "Farley", "Bernard", "Benjamin", "Travis", "Hayden", "Lynn", "Riddle", "David",
        "Duke", "Guy", "Michael", "Frederick", "Compton", "Raymond", "Stuart", "Holden", "Conrad", "Tyson", "Dale",
        "Burt", "Lindsay", "Noel", "Alastair", "Esther", "Abigail", "Betony", "Emma", "Alice", "Anna", "Arthur",
        "Adam", "Eugenia", "Maud", "Rosemary", "Luna", "Miranda", "River", "Ocean", "Freya", "Emmanuel", "Gabriel",
        "Harrier", "Athena", "Helen", "Monday", "Wednesday", "Friday", "Olivia", "Louise", "Lina", "Sofia", "Mia",
        "Juliette", "Jules", "Louis", "Liam", "Victor", "Gabriela", "Zhi", "Lihua", "Zhuo", "Gao", "Chao", "Ao",
        "Aidan", "Julia", "Claudia", "Anastasia", "Paolo", "Cosimo", "Sunday", "Summer",
        // first names: world / future
        "Voronya", "Malka", "Uther", "Zun", "Korshid", "Tir", "Bulan", "Kebe", "Temitope", "Nzube", "Ayomide",
        "Olabisi", "Kemi", "Chimdalu", "Idris", "Olawale", "Adewale", "Opeyemi", "Uche", "Ayodele", "Abaeze",
        "Zoputa", "Osa", "Oba", "Fana", "Genat", "Iskander", "Dawit", "Gabra", "Hakim", "Negasi", "Zala", "Yacob",
        "Lapis", "Hu", "Rania", "Sabra", "Nour", "Gamal", "Abasi", "Putri", "Intan", "Nurul", "Ayu", "Amalia", "Rio",
        "Fauzi", "Fajar", "Haliim", "Chaiya", "Chanthira", "Aran", "Anurak", "Hom", "Fa Ying", "Daw", "Bogdan",
        "Andon", "Boyana", "Filipa", "Gavril", "Lazar", "Rada", "Yoan", "Sava", "Nevena", "Zhivko", "Anh", "Linh",
        "Thanh", "Tuan", "Nam", "Trang", "Nat", "Vantu", "Khuyet", "Sim", "Aisha", "Amina", "Moussa", "Abdou",
        "Halima", "Zaliya", "Rama", "Marya", "Addo", "Zada", "Sama", "Salha", "Irene", "Farah", "Nadya", "Vania",
        "Nisa", "Kezia", "Farel", "Dimas", "Bayu", "Ansel", "Arinna", "Estan", "Ishtanu", "Ara", "Anath", "Chandra",
        "Allat", "Petra", "Nin", "Makeda", "Ezana", "Amon", "Ali", "Boudicca", "Cador", "Brigid", "Finnian",
        "Valeri", "Octavia", "Andronica", "Titus", "Tiberius", "Otho", "Julian", "Gaius", "Olympia", "Vittoria",
        "Catalina", "Amaru", "Drogo", "Lothar", "Clovis", "Mira", "Mir", "Kuma", "Soraya", "Juneau", "Neda",
        "Raheleh", "Future", "Nasa", "America", "Alaska", "Soyuz", "Volna", "Diziet", "Sol", "Pluto", "Eris",
        "Sedna", "Charon", "Ceres", "Mercury", "Mars", "Venus", "Ard", "Ares", "Jupiter", "Saturn", "Uranus",
        "Neptune", "Orion", "Orcus", "Varuna", "Io", "Europa", "Callisto", "Ganymede", "Titan", "Titania", "Triton",
        "Mimas", "Volta", "Miras", "Yurania", "Elim", "Trinity", "Egypt", "Persia", "Arafura", "Campion", "Adrana",
        "Purslane", "Galingale", "Hesperus", "Cadence", "Cascade", "Fescue", "Oberon", "Megas", "Megale", "Dajeil",
        "Canada", "Mexico", "Second", "Third", "Fourth", "Two", "Three", "Four", "Artifice", "Symbiote", "Ultra",
        "Perihelion", "Aphelion", "Hygiea", "Cybele", "Aurora", "Nil", "Zero", "Prozor", "Hasper", "Zosma", "Wezen",
        "Sheliak", "Meridiana", "Izar", "Vela", "Umbriel", "Nassun", "Essun", "Innon", "Schaffa", "Damaya", "Aeolis",
        "Chiron", "Nessus", "Arise", "Light", "Spring", "Autumn", "Winter", "Magnetic", "Thisbe", "Themis", "Psyche",
        "Eunomia", "Elektra", "Hebe", "Pyramus", "Kleopatra", "Urania", "Hektor", "Eulalia", "Volga", "Charm",
        "Neva", "Zec", "Altair", "Lucida", "Heka", "Tertia",
        // pirate
        "Sharkey", "Bones", "Skull", "Rage", "Radleak", "Glowing", "Airlock", "Trigger-finger", "Escape Pod",
        "Itchy", "Butcher", "Ray-gun", "Rusty", "Blood", "Hijack", "Radiation", "Bomber", "Antimatter", "Jolly",
        "Dancer", "Grifter", "High Burn", "Jump", "Ambush", "Gunstreet", "Sabot", "Slaughterhouse", "Socor",
        "Marietta", "Kennit", "Ultragamma", "Bombard", "Blitz", "Cuno", "Cunoesse", "Amra", "Pom", "Sham", "Naphi",
        "Cutter", "Snip", "Slash", "Shrapnel", "Zigzag", "Automatic", "Dysnomia", "Knife", "Luff", "Clew", "Jib",
        "Worm", "Sny", "Sprig", "Skeg", "Run", "Rig", "Deader", "Hex", "Five Aces", "Crossbones", "Plunder",
        "Massacre", "Furious", "Rotten", "Gold", "No Prisoners", "No-Credits", "Faller", "Master-blaster", "Jumper",
        "Madness", "Bloodwine", "Coldwind", "Sunrise", "Charming", "Maudite", "Turn", "Blackgoat", "Volley", "Salvo",
        "Shelby", "Rackham", "Mocker", "Shroak", "Rask", "Sirocco", "Crashblossom", "Flatline", "Millions",
        "Antigrav", "Screwfloss", "Blaze", "Scribb", "Slygrog", "Spinnaker", "Halyard", "Zabra", "Yuloh", "Yaw",
        "Wherry", "Voyol", "Tomkin", "Sweep", "Strake", "Spale", "Tafferel", "Scuttle", "Skcupper", "Sarve", "Rigol",
        "Quoin", "Prize", "Orlop", "Kevel", "Flitch", "Cascabel", "Carling", "Quick", "Runner", "Rumbustion",
        "Strongwater", "Screech",
        // luddic
        "Juniper", "Aneda", "Malichus", "Dawn", "Faith-my-joy", "Confidence", "Weep-not", "Deliverance", "Grace",
        "Zion", "Promise", "Mercy", "Redeem", "Favour", "Faith", "Sacrifice", "Praise-god", "Fear-god", "Makepeace",
        "Balance", "Silence", "Amity", "Verity", "Prudence", "Hope", "Virtue", "Experience", "Ashes", "Dust",
        "Blessing", "Amen", "Exult", "Dutiful", "Emerson", "Fall", "Elipha", "Cleophas", "Obedience", "Zachariah",
        "Obediah", "Thaddeus", "Cyrus", "Azariah", "Theophilus", "Virgil", "Malachi", "Anise", "Yarrow", "Marigold",
        "Jubal", "Zillah", "Mahlah", "Nemuel", "Oren", "Pinon", "Rezon", "Salu", "Uriel", "Pine", "Cypress",
        "Poplar", "Cedars", "Hemlock", "Humble", "Lively", "Seekwisdom", "Smallhope", "Barebones", "Memory",
        "Lament", "Given-to-god", "Greenearth", "Holyoak", "Psalm-of-earth", "Cherish-life", "Rite-of-earth",
        "Pleasant", "Shire", "Spruce", "Gardener", "Arkwright", "Latimer", "Laster", "Bolt", "Brazier", "Buckler",
        "Painter", "Carder", "Cutler", "Roper", "Salter", "Shriver", "Sixsmith", "Soper", "Stringer", "Threadgold",
        "Harrower", "Larimer", "Borozan", "Bondar", "Cioban", "Chayat", "Bijl", "Charbonnier", "Basumatary",
        "Carnera", "Bandler", "Calderone", "Quail", "Sage", "Chervil", "Chive", "Clove", "Fennel", "Galangal",
        "Mace", "Pepper", "Savory", "Sorrel", "Sumac", "Tarragon", "Thyme", "Wintergreen", "Summerblue", "Sorghum",
        "Barley", "Rye", "Teff", "Flax", "Burdock", "Chaparral", "Cress",
        // fringe
        "Tegus", "Arslar", "Nol", "Thrar", "Gal", "Shrial", "Boral", "Brux", "Dux", "Dol", "Dal", "Tor", "Gor",
        "Aladax", "Vor", "Bor", "Vox", "Tal", "Bok", "Teg", "Bur", "Qor", "Kor", "Vach", "Osol", "Delg", "Sur",
        "Sor", "Dulg", "Darg", "Gan", "Qach", "Chag", "Ulag", "Quch", "Dolg", "Bolg", "Balg", "Bruxa", "Duxal",
        "Dolal", "Dala", "Tora", "Gora", "Alada", "Voral", "Bora", "Voxa", "Talla", "Boka", "Tegga", "Burra", "Qora",
        "Kora", "Vacha", "Osola", "Delga", "Sural", "Sora", "Dulga", "Darga", "Gana", "Qacha", "Chaga", "Ulaga",
        "Qucha", "Dolga", "Bolga", "Balga", "Thrax", "Grak", "Braxx", "Adax", "Nox", "Prax", "Solax", "Zol",
        "Subedar", "Temujax", "Jamux", "Toghrol", "Qadax", "Arighar", "Batox", "Arslax", "Chechal", "Erdenol",
        "Batak", "Arighax", "Subedax", "Zor", "Surax", "Oyungar", "Oyungax", "Borax", "Shriax", "Altax", "Altarax",
        "Chagar", "Chagax", "Batax", "Batar", "Brutar", "Brutax", "Dochal", "Volgor", "Volgax", "Atalax", "Atalar",
        "Gurbax", "Gurbal", "Dologar", "Dologax", "Dalax", "Naimar", "Naimal", "Tabar", "Jagax", "Jagar", "Chinax",
        // myth
        "Thor", "Zeus", "Isis", "Ishtar", "Min", "Ra", "Osiris", "Anubis", "Set", "Hathor", "Wadjet", "Kali", "Enyo",
        "Minerva", "Varda", "Zorya", "Enki", "Gilgamesh", "Hercules", "Hector", "Paris", "Aeneas", "Artemis",
        "Hermes", "Poseidon", "Apollo", "Hera", "Ixchel", "Pollux", "Arcturus", "Ursa", "Chaac", "Cabrakan", "Akna",
        "Alom", "Gula", "Zarine", "Elysium", "Olympus", "Phobos", "Deimos", "Kyugu",
        // ai
        "Eureka", "Alabaster", "Crimson", "Amber", "Green", "Blue", "White", "Fuchsia", "Yellow", "Gray", "Azure",
        "Silver", "Black", "Magenta", "Violet", "Ultraviolet", "Infrared", "Cerulean", "Maroon", "Brown", "Burgundy",
        "Orange", "Celadon", "Cerise", "Chartreuse", "Cyan", "Puce", "Scarlet", "Sienna", "Aquamarine", "Mauve",
        "Ebony", "Viridian", "Gunmetal", "Icterine", "Octarine", "Ecru", "Indigo", "Ivory", "Jade", "Lavender",
        "Navy", "Ochre", "Heliotrope", "Olive", "Onyx", "Pearl", "Umber", "Ruby", "Russet", "Saffron", "Sapphire",
        "Slate", "Teal", "Turquoise", "Ultramarine", "Vermillion", "Exalted", "Melodic", "Ancient", "Velocious",
        "Tenacious", "Sagacious", "Draconian", "Prevailing", "Palatial", "Optimal", "Paramount", "Transcendent",
        "Ascendant", "Palatinate", "Ethereal", "Intangible", "Inscrutable", "Resplendent", "Glorious", "Radiant",
        "Splendorous", "Unequaled", "Boundless", "Endless", "Infinite", "Unthinkable", "Ultimate", "Terminal",
        "Optimum", "Superlative", "Supreme", "Wintermute", "Ghostwheel", "Xerxes"
    )

    // Faction ship-name prefixes (shipNamePrefix in .faction files), with the trailing space seen in ship names
    val shipNamePrefixes: List<String> = listOf(
        "SDS ", "TTDS ", "PLS ", "01011001 ", "CGR ", "LGS ", "LSS ", "RSDS ", "DSS ", "TTS ", "HSS ", "ISS "
    )

    // Rarely used: fleet/person memory keys, fleet types, variant tags
    val rareKeys: List<String> = listOf(
        "\$resourceSalvageMult", "\$makeNonHostileTakesPriority", "\$tempNotAvoidingAbyssal",
        "\$avoidingAbyssalHyperspace", "\$exceptionalSleeperPodOfficerOnSectorGen",
        "\$canOnlyBeEngagedWhenVisibleToPlayer", "\$stationBaseFleet", "\$stationMarket", "\$core_fightToTheLast",
        "\$fleetBuysStuff", "\$fleetBuysStuffRate", "\$skipTInfo", "\$cfai_makeFleePlayer",
        "\$sawPlayerTransponderOff", "\$sawPlayerTransponderOn", "\$alwaysSpreadTOffHostility",
        "\$shownFleetDescAlready", "\$overrideNoAutofit", "\$requiresDiscretionToDeal",
        "\$willHasslePlayer", "\$hassleType", "\$doNotTryToAvoidNearbyFleets", "\$shrinePilgrimFleet",
        "\$academyFleet", "\$mayGoIntoAbyss", "\$officerSkillPicksPerLevel", "\$officerMaxLevel",
        "\$officerMaxEliteSkills", "\$exceptionalSleeperPodOfficer", "\$suspectedAI", "\$story_critical",
        "\$patrolExtraSuspicion", "\$cfai_doNotIgnorePlayer", "\$cfai_ignoreOtherFleets",
        "\$cfai_ignoredByOtherFleets", "\$cfai_ignoredByFaction", "\$cfai_patrolDist", "\$stationFleet",
        "\$missionImportant", "\$core_fleetNoMilitaryResponse", "\$core_fleetBusy", "\$core_fleetSpecialAction",
        "\$core_fleetMilitaryResponse", "\$core_fleetChasingGhost", "\$core_fleetNotChasingGhost",
        "\$core_fleetChasingGhostRandom", "\$patrolAllowTOff", "\$noShipRecovery", "\$noShipDerelictsPostBattle",
        "\$military", "\$patrol", "\$sourceMarket", "\$isPatrol", "\$isWarFleet", "\$isRaider",
        "\$isCustomsInspector", "\$isTradeFleet", "\$isScavenger", "\$isSmuggler", "\$isPirate", "\$fleetType",
        "\$doNotGetSidetracked", "\$forceTOff", "\$cfai_avoidPlayerSlowly", "\$cfai_neverAvoidPlayerSlowly",
        "\$cfai_makeHostile", "\$cfai_makeHostileToPlayerTradeFleets", "\$cfai_makeHostileToAllTradeFleets",
        "\$cfai_makeHostileWhileTOff", "\$cfai_makeNonHostile", "\$cfai_allowPlayerBattleJoinTOff",
        "\$cfai_makeAlwaysPursue", "\$cfai_makePreventDisengage", "\$cfai_makeAllowDisengage",
        "\$cfai_makeAggressive", "\$cfai_holdVsStronger", "\$cfai_makeAggressiveLastsOneBattle",
        "\$cfai_makeNonAggressive", "\$cfai_recentlyDefeatedByPlayer", "\$cfai_noJump", "\$cfai_longPursuit",
        "\$ignorePlayerCommRequests", "\$pursuePlayer", "\$keepPursuingPlayer", "\$lowRepImpact", "\$noRepImpact",
        "\$everyoneJoinsBattleAgainst", "\$claimingFaction", "\$doNotInitCommListings", "foodReliefFleet",
        "shrinePilgrims", "academyFleet", "tradeSmuggler", "smallTrader", "tradeLiner", "patrolSmall",
        "patrolMedium", "patrolLarge", "sacredProtectors", "investigators", "leagueEnforcer", "leagueArmada",
        "supplyFleet", "commerceRaiders", "raider", "inspectionFleet", "taskForce", "scavengerSmall",
        "scavengerMedium", "scavengerLarge", "mercScout", "mercBountyHunter", "mercPrivateer", "mercPatrol",
        "mercArmada", "battlestation", "personBounty", "skip_for_default_hull_modules",
        "variant_allow_excess_op_etc", "variant_always_retain_smods_on_salvage", "consistent_weapon_drops",
        "always_recoverable", "unboardable", "unrestorable", "fx_drone", "military", "trade", "underworld",
        "science", "pather", "ai_core", "no_ai_core_drop",
        // Person memory keys
        "\$Post", "\$Rank", "\$id", "\$importance", "\$importanceAtLeastHigh", "\$importanceAtMostLow",
        "\$isContact", "\$isPerson", "\$level", "\$menuState", "\$name", "\$option", "\$personName",
        "\$personality", "\$post", "\$postAOrAn", "\$postId", "\$rank", "\$rankAOrAn", "\$rankId",
        "\$relName", "\$rel", "\$relValue"
    )

    // Portraits
    val portraits: List<String> = listOf(
        "graphics/portraits/portrait12.png", "graphics/portraits/portrait13.png",
        "graphics/portraits/portrait14.png", "graphics/portraits/portrait15.png",
        "graphics/portraits/portrait16.png", "graphics/portraits/portrait17.png",
        "graphics/portraits/portrait18.png", "graphics/portraits/portrait20.png",
        "graphics/portraits/portrait21.png", "graphics/portraits/portrait22.png",
        "graphics/portraits/portrait23.png", "graphics/portraits/portrait24.png",
        "graphics/portraits/portrait25.png", "graphics/portraits/portrait26.png",
        "graphics/portraits/portrait27.png", "graphics/portraits/portrait28.png",
        "graphics/portraits/portrait29.png", "graphics/portraits/portrait30.png",
        "graphics/portraits/portrait31.png", "graphics/portraits/portrait32.png",
        "graphics/portraits/portrait33.png", "graphics/portraits/portrait34.png",
        "graphics/portraits/portrait35.png", "graphics/portraits/portrait36.png",
        "graphics/portraits/portrait37.png", "graphics/portraits/portrait38.png",
        "graphics/portraits/portrait39.png", "graphics/portraits/portrait40.png",
        "graphics/portraits/portrait41.png", "graphics/portraits/portrait42.png",
        "graphics/portraits/portrait43.png", "graphics/portraits/portrait44.png",
        "graphics/portraits/portrait45.png", "graphics/portraits/portrait46.png",
        "graphics/portraits/portrait47.png", "graphics/portraits/portrait48.png",
        "graphics/portraits/portrait49.png", "graphics/portraits/portrait_ai1.png",
        "graphics/portraits/portrait_ai1b.png", "graphics/portraits/portrait_ai2.png",
        "graphics/portraits/portrait_ai2b.png", "graphics/portraits/portrait_ai3b.png",
        "graphics/portraits/portrait_corporate01.png", "graphics/portraits/portrait_corporate02.png",
        "graphics/portraits/portrait_corporate03.png", "graphics/portraits/portrait_corporate04.png",
        "graphics/portraits/portrait_corporate05.png", "graphics/portraits/portrait_corporate06.png",
        "graphics/portraits/portrait_corporate07.png", "graphics/portraits/portrait_corporate08.png",
        "graphics/portraits/portrait_corporate09.png", "graphics/portraits/portrait_corporate10.png",
        "graphics/portraits/portrait_corporate11.png", "graphics/portraits/portrait_corporate12.png",
        "graphics/portraits/portrait_corporate13.png", "graphics/portraits/portrait_corporate14.png",
        "graphics/portraits/portrait_defactioned00.png", "graphics/portraits/portrait_defactioned01.png",
        "graphics/portraits/portrait_defactioned02.png", "graphics/portraits/portrait_defactioned03.png",
        "graphics/portraits/portrait_defactioned04.png", "graphics/portraits/portrait_defactioned05.png",
        "graphics/portraits/portrait_defactioned06.png", "graphics/portraits/portrait_defactioned07.png",
        "graphics/portraits/portrait_diktat01.png",
        "graphics/portraits/portrait_diktat02.png", "graphics/portraits/portrait_diktat03.png",
        "graphics/portraits/portrait_diktat04.png", "graphics/portraits/portrait_diktat05.png",
        "graphics/portraits/portrait_diktat06.png", "graphics/portraits/portrait_diktat07.png",
        "graphics/portraits/portrait_diktat08.png", "graphics/portraits/portrait_diktat09.png",
        "graphics/portraits/portrait_diktat10.png", "graphics/portraits/portrait_diktat11.png",
        "graphics/portraits/portrait_diktat12.png", "graphics/portraits/portrait_diktat13.png",
        "graphics/portraits/portrait_diktat14.png", "graphics/portraits/portrait_diktat15.png",
        "graphics/portraits/portrait_diktat16.png", "graphics/portraits/portrait_generic_grayscale.png",
        "graphics/portraits/portrait_hegemony01.png",
        "graphics/portraits/portrait_hegemony02.png", "graphics/portraits/portrait_hegemony03.png",
        "graphics/portraits/portrait_hegemony04.png", "graphics/portraits/portrait_hegemony05.png",
        "graphics/portraits/portrait_hegemony06.png", "graphics/portraits/portrait_hegemony07.png",
        "graphics/portraits/portrait_hegemony08.png", "graphics/portraits/portrait_hegemony09.png",
        "graphics/portraits/portrait_hegemony10.png", "graphics/portraits/portrait_hegemony11.png",
        "graphics/portraits/portrait_hegemony12.png", "graphics/portraits/portrait_hegemony13.png",
        "graphics/portraits/portrait_hegemony14.png", "graphics/portraits/portrait_hegemony15.png",
        "graphics/portraits/portrait_hegemony16.png", "graphics/portraits/portrait_hegemony17.png",
        "graphics/portraits/portrait_league00.png", "graphics/portraits/portrait_league01.png",
        "graphics/portraits/portrait_league02.png", "graphics/portraits/portrait_league03.png",
        "graphics/portraits/portrait_league04.png", "graphics/portraits/portrait_league05.png",
        "graphics/portraits/portrait_league06.png", "graphics/portraits/portrait_league07.png",
        "graphics/portraits/portrait_league08.png", "graphics/portraits/portrait_league09.png",
        "graphics/portraits/portrait_league10.png", "graphics/portraits/portrait_league11.png",
        "graphics/portraits/portrait_league12.png", "graphics/portraits/portrait_league13.png",
        "graphics/portraits/portrait_league14.png", "graphics/portraits/portrait_luddic00.png",
        "graphics/portraits/portrait_luddic01.png", "graphics/portraits/portrait_luddic02.png",
        "graphics/portraits/portrait_luddic03.png", "graphics/portraits/portrait_luddic04.png",
        "graphics/portraits/portrait_luddic05.png", "graphics/portraits/portrait_luddic06.png",
        "graphics/portraits/portrait_luddic07.png", "graphics/portraits/portrait_luddic08.png",
        "graphics/portraits/portrait_luddic09.png", "graphics/portraits/portrait_luddic10.png",
        "graphics/portraits/portrait_luddic11.png", "graphics/portraits/portrait_luddic12.png",
        "graphics/portraits/portrait_luddic13.png", "graphics/portraits/portrait_luddic14.png",
        "graphics/portraits/portrait_luddic15.png", "graphics/portraits/portrait_luddic16.png",
        "graphics/portraits/portrait_mercenary01.png", "graphics/portraits/portrait_mercenary02.png",
        "graphics/portraits/portrait_mercenary03.png", "graphics/portraits/portrait_mercenary04.png",
        "graphics/portraits/portrait_mercenary05.png", "graphics/portraits/portrait_mercenary06.png",
        "graphics/portraits/portrait_mercenary07.png", "graphics/portraits/portrait_mercenary08.png",
        "graphics/portraits/portrait_pirate01.png", "graphics/portraits/portrait_pirate02.png",
        "graphics/portraits/portrait_pirate03.png", "graphics/portraits/portrait_pirate04.png",
        "graphics/portraits/portrait_pirate05.png", "graphics/portraits/portrait_pirate06.png",
        "graphics/portraits/portrait_pirate07.png", "graphics/portraits/portrait_pirate08.png",
        "graphics/portraits/portrait_pirate09.png", "graphics/portraits/portrait_pirate10.png",
        "graphics/portraits/portrait_pirate11.png", "graphics/portraits/portrait_pirate12.png",
        "graphics/portraits/portrait_pirate13.png", "graphics/portraits/portrait_pirate14.png",
        "graphics/portraits/portrait_pirate15.png", "graphics/portraits/portrait_pirate16.png",
        "graphics/portraits/portrait_pirate17.png", "graphics/portraits/portrait_pirate18.png",
        "graphics/portraits/portrait_pirate19.png", "graphics/portraits/portrait_pirate20.png",
        "graphics/portraits/portrait_pirate21.png", "graphics/portraits/portrait_pirate22.png",
        "graphics/portraits/threat.png"
    )

    // Fighter wings
    val wings: List<String> = listOf(
        "aspect_attack_wing", "aspect_missile_wing", "aspect_shieldbreaker_wing", "aspect_shock_wing",
        "attack_swarm_wing", "borer_wing", "construction_swarm_wing", "mining_drone_wing", "reclamation_swarm_wing",
        "sarissa_wing", "shrouded_ejecta_wing", "shrouded_vortex_wing", "terminator_wing", "cobra_wing", "wasp_wing",
        "xyphos_wing", "lux_wing", "claw_wing", "spark_wing", "trident_wing", "gladius_wing", "longbow_wing",
        "warthog_wing", "dagger_wing", "flash_wing", "perdition_wing", "piranha_wing", "talon_wing", "thunder_wing",
        "hoplon_wing", "broadsword_wing"
    )

    // Hull ids (incl. skins), unused-in-stock first, then ascending stock usage
    val hulls: List<String> = listOf(
        "broadsword", "claw", "cobra", "dagger", "dem_drone", "drone_assault", "drone_borer", "drone_pd",
        "drone_pd_midline", "drone_sensor", "drone_terminator", "gladius", "kite_d", "longbow", "manticore_pather",
        "mining_drone", "perdition", "piranha", "remnant_station1", "sarissa", "shuttlepod", "station2", "station3",
        "talon", "talon_tritachyon", "thunder", "trident", "warthog", "wasp", "xyphos", "afflictor",
        "afflictor_d_pirates", "anubis", "atlas", "atlas2", "bastillon", "berserker", "brawler_LG", "brawler_pather",
        "brawler_tritachyon", "buffalo", "buffalo_d", "buffalo_hegemony", "buffalo_luddic_church", "buffalo_pirates",
        "buffalo_tritachyon", "centurion_LG", "cerberus_d", "cerberus_luddic_path", "colossus", "colossus2",
        "colossus3", "crig", "defender", "derelict_mothership_bow_module", "derelict_mothership_pod_engine_left",
        "derelict_mothership_pod_m_left", "derelict_mothership_pod_m_right", "derelict_mothership_pod_s_right",
        "derelict_mothership_sensor_mast", "dominator_d", "dominator_xiv", "dram", "eagle_d", "eagle_xiv",
        "enforcer_d", "enforcer_d_pirates", "enforcer_xiv", "fabricator_unit", "falcon_d", "falcon_p", "flash",
        "gargoyle", "gemini", "gremlin", "gremlin_d_pirates", "gremlin_luddic_path", "hammerhead_d", "harbinger",
        "hermes_d", "hive_unit", "hoplon", "hound_d", "hound_hegemony", "hound_luddic_church", "hound_luddic_path",
        "kite_pirates", "lasher_d", "lasher_luddic_church", "lasher_pather", "lumen", "lux", "manticore_luddic_path",
        "mercury_d", "merlon", "module_armour1", "module_armour2", "module_armour3", "module_armour4",
        "module_armour5", "module_bastion1_lowtech", "module_bastion_pd1", "module_blocker1", "module_blocker2",
        "module_blocker2b", "module_blocker2c", "module_citadel1_lowtech", "module_decor_midline1",
        "module_decor_midline2", "module_hightech_armour", "module_hightech_attack",
        "module_hightech_attack_minelayer", "module_hightech_citadel", "module_hightech_hangar",
        "module_hightech_shield", "module_hightech_strut", "module_large1_lowtech", "module_large2_lowtech",
        "module_midline_broadside", "module_midline_citadel", "module_midline_support",
        "module_onslaught_armor_left", "module_onslaught_armor_right", "mudskipper", "mule_d", "nebula", "odyssey",
        "omen", "onslaught_mk1", "onslaught_xiv", "overseer_unit", "ox", "phaeton", "phantom", "picket",
        "prometheus", "prometheus2", "rampart", "ravelin", "remnant_armour1", "remnant_armour2", "remnant_armour3",
        "remnant_shield1", "remnant_shield2", "remnant_weapon_platform1", "remnant_weapon_platform2",
        "remnant_weapon_platform3", "retribution", "revenant", "sentry", "shade", "shade_d_pirates",
        "shrike_pirates", "shrouded_ejecta", "shrouded_eye", "shrouded_maelstrom", "shrouded_maw",
        "shrouded_tendril", "shrouded_vortex", "spark", "starliner", "station1", "station1_hightech",
        "station1_midline", "station2_hightech", "station2_midline", "station3_hightech", "station3_midline",
        "station_derelict_survey_mothership", "sunder_d", "tarsus", "tarsus_d", "valkyrie", "venture_p",
        "venture_pather", "warden", "wolf_d", "wolf_d_pirates", "apogee", "assault_unit", "brilliant", "buffalo2",
        "centurion", "eagle_LG", "eradicator_pirates", "executor", "falcon_LG", "falcon_xiv", "fulgent", "fury",
        "glimmer", "grendel", "hammerhead_LG", "hermes", "hound", "hyperion", "invictus", "kite", "kite_luddic_path",
        "kite_original", "legion_xiv", "manticore_pirates", "module_midline_bastion", "monitor", "mudskipper2",
        "mule_d_pirates", "nova", "pegasus", "platform1", "remnant_hangar1", "remnant_station2", "scarab",
        "scintilla", "shepherd", "shrike", "skirmish_unit", "station_small", "sunder_LG", "tempest",
        "vanguard_pirates", "wayfarer", "astral", "attack_swarm", "cerberus_d_pirates", "condor", "conquest", "doom",
        "drover", "eagle", "falcon", "gryphon", "hound_d_pirates", "kite_hegemony", "mule", "onslaught", "paragon",
        "radiant", "standoff_unit", "venture", "wolf_hegemony", "ziggurat", "apex", "aspect", "brawler", "cerberus",
        "heron", "legion", "manticore", "medusa", "mercury", "mora", "shard_right", "sunder", "vanguard", "aurora",
        "dominator", "champion", "facet", "hammerhead", "shard_left", "tesseract", "wolf", "eradicator", "lasher",
        "vigilance", "enforcer"
    )

    // Weapons: ascending stock usage
    val weapons: List<String> = listOf(
        "abyssal_glare", "blinker_green", "blinker_red", "bomb", "canister_flak", "clusterbomb", "dragon_payload",
        "flarelauncher1", "flarelauncher2", "flarelauncher21", "flarelauncher3", "fragbomb", "gazer_payload",
        "gorgon_payload", "hammer_single", "hydra_payload", "interdictorbeam", "ioncannon_fighter", "lidardish",
        "lightmortar_fighter", "lights_buffalo", "lights_hound", "minelayer1", "minelayer2", "motelauncher",
        "motelauncher_hf", "nb_bomblauncher", "novalaser", "od_bomblauncher", "pdburst_fighter", "pusherplate_lt",
        "pusherplate_lt_small", "pusherplate_nova", "rift_lightning_minelayer", "riftbeam_minelayer",
        "riftcascade_minelayer", "riftlance_minelayer", "sabot_fighter", "sabot_single", "sensordish",
        "swarmer_fighter", "targetinglaser1", "targetinglaser2", "targetinglaser3", "terminator_missile",
        "gorgonpod", "irpulse_fighter", "phasecl_bomber", "realitydisruptor", "rifttorpedo", "annihilator_fighter",
        "dragon", "heavy_adjudicator", "pilum_large", "riftcascade", "swarm_launcher", "vpdriver", "atropos_single",
        "gigacannon", "pseudoparticle_jet", "devouring_swarm", "heavyburst", "jackhammer", "mininglaser", "phasecl",
        "rift_lightning", "voidblaster", "vortex_launcher", "cryoflux", "cyclone", "gazerpod", "gorgon",
        "hammerrack", "hydra", "salamanderpod", "assaying_rift", "harpoon_single", "multineedler",
        "neoferric_quadcoil", "neutron_torpedo", "voltaic_cannon", "locust", "cryoblaster", "dragonpod", "hil",
        "tpc", "riftbeam", "seeker_fragment", "unstable_fragment", "atropos", "gauss", "guardian", "hellbore",
        "kinetic_fragments", "kineticblaster", "miningblaster", "riftlance", "hurricane", "gazer", "heavyblaster",
        "mjolnir", "disintegrator", "heavy_mass_driver", "hephag", "irautolance", "plasma", "amblaster",
        "voltaic_discharge", "breachpod", "squall", "autopulse", "heavymg", "resonatormrm", "inimical_emanation",
        "phasebeam", "shredder", "chaingun", "heavymauler", "amsrm", "harpoonpod", "heavymortar", "tachyonlance",
        "breach", "devastator", "railgun", "annihilatorpod", "ionbeam", "lightmortar", "ionpulser", "dualflak",
        "light_mass_driver", "minipulser", "heavyneedler", "lightdualac", "swarmer", "pilum", "lightneedler",
        "mark9", "annihilator", "lrpdlaser", "typhoon", "heatseeker", "ioncannon", "hveldriver", "lightag",
        "sabotpod", "hammer", "irpulse", "arbalest", "reaper", "shockrepeater", "pulselaser", "lightdualmg",
        "taclaser", "lightac", "heavyac", "sabot", "gravitonbeam", "harpoon", "pdburst", "lightmg", "flak",
        "pdlaser", "vulcan"
    )

    // Skills
    val skills: List<String> = listOf(
        "helmsmanship", "combat_endurance", "impact_mitigation", "damage_control", "field_modulation",
        "point_defense", "target_analysis", "ballistic_mastery", "systems_expertise", "missile_specialization",
        "tactical_drills", "coordinated_maneuvers", "wolfpack_tactics", "crew_training", "fighter_uplink",
        "carrier_group", "officer_training", "officer_management", "best_of_the_best", "support_doctrine",
        "force_concentration", "navigation", "sensors", "gunnery_implants", "energy_weapon_mastery",
        "electronic_warfare", "flux_regulation", "cybernetic_augmentation", "phase_corps", "special_modifications",
        "neural_link", "automated_ships", "bulk_transport", "salvaging", "field_repairs", "ordnance_expert",
        "polarized_armor", "containment_procedures", "makeshift_equipment", "industrial_planning",
        "derelict_contingent", "hull_restoration", "omega_ecm", "hypercognition", "space_operations",
        "planetary_operations", "colony_management", "strike_commander", "ranged_specialization",
        "shield_modulation", "phase_mastery", "reliability_engineering", "weapon_drills", "auxiliary_support",
        "aptitude_combat", "aptitude_leadership", "aptitude_technology", "aptitude_industry"
    )

    // Ranks
    val ranks: List<String> = listOf(
        "artificialIntelligence", "fugitive",
        "houseLeader", "houseLeaderMale", "houseLeaderFemale", "spaceSailor", "spaceChief", "spaceEnsign",
        "spaceCaptain", "spaceAdmiral", "spaceMarshal", "groundPrivate", "groundSergeant", "groundLieutenant",
        "groundCaptain", "groundMajor", "groundColonel", "groundGeneral", "knightCaptain", "chiefHighInspector",
        "deputyStarMarshal", "guardHighDeputyExecutor", "pilot", "brother", "sister", "father", "mother", "elder",
        "freedomFighter", "clone"
    )

    // Posts
    val posts: List<String> = listOf(
        "personnelOfficer", "underworldBoss", "exile", "assistant_academician", "associate_academician",
        "alphaCore", "betaCore", "gammaCore",
        "factionLeader", "patrolCommander", "fleetCommander", "baseCommander", "stationCommander",
        "outpostCommander", "portmaster", "intelligenceDirector", "guardLeader", "supplyOfficer", "supplyManager",
        "medicalSupplier", "administrator", "militaryAdministrator", "freeAdmin", "guildMaster", "communeLeader",
        "executive", "seniorExecutive", "minorCriminal", "criminal", "spacer", "genericMilitary",
        "marineSquadLeader", "trader", "merchant", "investor", "commodities_agent", "entrepreneur",
        "nanoforge_engineer", "shady", "gangster", "smuggler", "fence", "armsDealer", "citizen", "boss", "crewBoss",
        "officer_for_hire", "investigator", "agent", "specialAgent", "provost", "academician", "securityChief",
        "doctor", "hacker", "hermit", "pensioner", "activist", "scientist", "terrorist", "warlord", "unknown",
        "aristocrat", "curate", "synodSubcurate", "subcurate", "archcurate", "heretic", "shrinePriest",
        "excubitorOrbis", "initiate", "novice", "pilgrim"
    )

    // Hullmods: ascending stock usage
    val hullMods: List<String> = listOf(
        "ablative_armor", "adaptive_coils", "adaptiveshields", "additional_berthing", "advanced_ground_support",
        "advancedcore", "always_detaches",
        "anchorrotation", "andrada_mods", "assault_package", "axialrotation", "bdeck", "chassis_storage", "civgrade",
        "coherer", "comp_armor", "comp_hull", "comp_storage", "converted_bay", "converted_fighterbay",
        "damaged_deck", "damaged_mounts", "defective_manufactory", "degraded_drive_field", "degraded_life_support",
        "degraded_shields", "delicate", "design_compromises", "destroyed_mounts", "distributed_fire_control",
        "do_not_back_off", "do_not_fire_through", "drive_field_stabilizer", "dweller_hullmod", "ecm",
        "erratic_injector", "escort_package", "ex_phase_coils", "expanded_cargo_holds", "faulty_auto", "fluxshunt",
        "fourteenth", "fragile_subsystems", "glitched_sensors", "ground_support", "hbi", "heg_militarized",
        "high_maintenance", "high_scatter_amp", "hiressensors", "ill_advised", "increased_maintenance",
        "low_maintenance", "malfunctioning_comms", "manual_systems", "missile_reload", "nav_relay",
        "neural_integrator", "neural_interface",
        "never_detaches", "no_weapon_flux", "operations_center", "phase_anchor", "phasefield", "reduced_explosion",
        "repair_gantry", "rugged", "shard_spawner", "shared_flux_sink", "shield_always_on", "shielded_holds",
        "shrouded_lens", "shrouded_mantle", "shrouded_thunderhead", "solar_shielding", "stealth_minefield",
        "stealth_minefield_lt", "supercomputer", "surveying_equipment", "terminator_core", "threat_hullmod",
        "tow_cable", "unstable_coils", "vast_hangar", "vastbulk", "auxiliary_fuel_tanks",
        "defensive_targeting_array", "frontemitter", "militarized_subsystems", "recovery_shuttles",
        "augmentedengines", "automated", "comp_structure", "degraded_engines", "faulty_grid", "efficiency_overhaul",
        "fragment_coordinator", "frontshield", "high_frequency_attractor", "secondary_fabricator", "shield_shunt",
        "advancedshieldemitter", "autorepair", "extendedshieldemitter", "expanded_deck_crew", "pointdefenseai",
        "fragment_swarm", "converted_hangar", "missile_autoloader", "ballistic_rangefinder",
        "stabilizedshieldemitter", "hardened_subsystems", "reinforcedhull", "safetyoverrides", "eccm", "fluxcoil",
        "turretgyros", "unstable_injector", "advancedoptics", "insulatedengine", "blast_doors",
        "hardenedshieldemitter", "auxiliarythrusters", "magazines", "dedicated_targeting_core", "armoredweapons",
        "fluxbreakers", "heavyarmor", "fluxdistributor", "missleracks", "targetingunit"
    )

    // Factions
    val factions: List<String> = listOf(
        "diktat_achilles",
        "neutral", "player", "pirates", "hegemony", "independent", "tritachyon", "sindrian_diktat", "lions_guard",
        "knights_of_ludd", "luddic_church", "luddic_path", "persean_league", "derelict", "remnants", "omega",
        "threat", "dweller", "scavengers", "sleeper", "poor", "mercenary"
    )

    // Variant naming
    val variantNaming: List<String> = listOf(
        "_Hull", "_Standard", "_Strike", "_Assault", "_Attack", "_Support", "_Elite", "_Starting", "_Balanced",
        "_Overdriven", "_Escort", "_Outdated", "_Custom", "Close Support", "Fire Support", "Shieldbreaker",
        "Overdriven", "Outdated", "Escort", "Balanced", "Elite", "Support", "Assault", "Strike", "Attack", "Custom",
        "Standard"
    )

    // Officer basics
    val officerBasics: List<String> = listOf(
        "omega_core", "gamma_core", "beta_core", "alpha_core", "reckless", "aggressive", "steady", "cautious",
        "timid", "spaceLieutenant", "spaceCommander", "officer"
    )

    // Weapon slot ids: ascending usage
    val slotIds: List<String> = listOf(
        "WS 028", "WS 029", "WS 031", "WS 032", "WS 026", "WS 024", "WS 022", "WS 023", "WS 021", "WS 019", "WS 017",
        "WS 020", "WS 018", "WS 016", "WS 015", "WS 014", "WS 013", "WS 012", "WS 011", "WS 010", "WS 000", "WS 009",
        "WS 008", "WS 007", "WS 006", "WS 005", "WS 004", "WS 003", "WS 002", "WS 001"
    )

    // NEVER edit this list after codes have shipped. Make a new version instead.
    val v1: ByteArray = build(
        personNames + rareKeys + portraits + wings + hulls + weapons + skills + ranks + posts +
                hullMods + factions + shipNamePrefixes + variantNaming + officerBasics + slotIds
    )
}