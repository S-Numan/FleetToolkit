package fleetkit.serialization.dictionaries

import fleetkit.serialization.core.ByteBuffers

/**
 * Preset zlib dictionary for serialized VariantAPI / FleetMemberAPI / PersonAPI / CampaignFleetAPI data.
 *
 * NEVER edit this once codes have shipped. Make a new version instead.
 */
object PackDictionaryV2 {
    /** Builds a dictionary from strings ordered least common -> most common. Max 32 KB. */
    fun build(strings: List<String>): ByteArray {
        val out = ByteBuffers.Out()
        for (s in strings) out.writeUTF(s)
        val bytes = out.toByteArray()
        // zlib only uses the last 32 KB, so drop the front (least common) if too large
        return if (bytes.size > 32768) bytes.copyOfRange(bytes.size - 32768, bytes.size) else bytes
    }

    // Tier 0 (5465 bytes): expected saving per byte 0.01 .. 0.02
    private val tier0: List<String> = listOf(
        // faction ids
        "derelict",
        // memory keys
        "\$isCustomsInspector", "\$sawPlayerTransponderOn", "\$fleetBuysStuff", "\$isWarFleet", "\$core_hiddenBase",
        "\$cfai_makeAggressiveLastsOneBattle", "\$cfai_avoidPlayerSlowly", "\$requiresDiscretionToDeal",
        "\$core_fleetNoMilitaryResponse",
        // person names
        "Arise", "Yurania", "Umbriel", "Trinity", "Sheliak", "Schaffa", "Cascade", "Campion", "Cadence", "Arafura",
        "America", "#Baikal", "Warren", "Walter", "Travis", "Thomas", "Tanner", "Stuart", "Spence", "Riddle",
        "Pierce", "Oliver", "Norton", "Norman", "Nelson", "Morgan", "Martin", "Joseph", "Jordan", "Howard", "Holden",
        "Hayden", "Gordon", "George", "Farley", "Dudley", "Dennis", "Curtis", "Conrad", "Conner", "Clarke", "Carson",
        "Ashley", "Arnold", "Eugenia", "Abigail", "Calderone", "Chanthira", "Andronica", "Miramichi", "Hochelaga",
        "Viridian", "Sapphire", "Octarine", "Lavender", "Infrared", "Icterine", "Gunmetal", "Cerulean", "Burgundy",
        "Makeda", "Juneau", "Zarine", "Wadjet", "Pollux", "Ixchel", "Ishtar", "Hermes", "Apollo", "Basumatary",
        "Rosemary", "Zaurak", "Symbiote", "Purslane", "Artifice", "Aphelion", "Charbonnier", "Turquoise",
        "Alabaster", "Olivia", "Cosimo", "William", "Whitney", "Terrell", "Stewart", "Spencer", "Solomon", "Sherman",
        "Richard", "Raymond", "Randall", "Patrick", "Michael", "Lindsey", "Lindsay", "Leonard", "Kennedy", "Jackson",
        "Gregory", "Gilbert", "Francis", "Elliott", "Douglas", "Compton", "Charles", "Cameron", "Bradley", "Bernard",
        "Bennett", "Allison", "Bulan", "Meridiana", "Galingale", "Vermillion", "Heliotrope", "Chartreuse",
        "Aquamarine", "Second", "Persia", "Lucida", "Fourth", "Altair", "Chandra", "Artemis", "Winter", "Spring",
        "Autumn", "Ultramarine", "Monday", "Friday", "Perihelion", "Lawrence",
        // portraits
        "graphics/portraits/portrait19.png", "graphics/portraits/portrait_ai2.png",
        "graphics/portraits/portrait_ai1.png", "graphics/portraits/portrait_generic_grayscale.png",
        "graphics/portraits/portrait49.png", "graphics/portraits/portrait24.png",
        // ship names
        "Cador", "Lion's Fang", "Lion's Claw", "King Cygnus", "Executioner", "Torment", "Inferno", "Gehenna",
        "Crusade", "Hector de Maris", "Elyan the White", "Yacatecuhtli", "Quetzalcoatl", "Xi-Fido", "Puma-Er",
        "Phi-Rex", "Lion's Pride", "Leaflight", "Diligence", "Whydragon", "Vesperiad", "Trooperon", "Sparrowen",
        "Sorcitude", "Sorceptor", "Scrivener", "Scrimshaw", "Santianna", "Sad Witch", "Rho-Priah", "Red Forge",
        "Ning Shim", "Mogularer", "Miraculux", "Long Shot", "Lone Star", "Laserhawk", "Lamentite", "Kosmonara",
        "Hyperitas", "Home Away", "Hlad-Zero", "Gray Area", "Freedon't", "Eigengrau", "Chon-Echo", "Blue Mars",
        "Billiatee", "Ark Matet", "Ard Arrow", "Appa-Duke", "Ambergris", "Midas", "Shabrang", "Chamrosh", "Mars",
        "Prince Regent", "Novus Temujin", "Leader's Will", "Battle", "Victory", "Trooper", "Triumph", "Tracker",
        "Defiant", "Buzzard", "Bulwark", "Aurochs", "Aquilon", "Adamant", "Ambush", "Novus Napoleon",
        "Great Dictator", "End of History", "Dawn of Empire", "Chalmecacihuilt", "Hate-sin", "Nova", "White Pine",
        "Waning Day", "Temperance", "Revelation", "Redemption", "Green Mars", "Voidclipse", "Vikinguard",
        "Tail Folly", "Septentrio", "Sabled Sun", "Roman Road", "On Program", "Omnichrome", "Old Zodiac",
        "Oh, Raptor", "Nivek Ogre", "Nightwitch", "Mog Ruitas", "Malfeasant", "Lightwitch", "Kemwer One",
        "Juroi Mars", "Hypericity", "Ghost Bird", "Foravenger", "Duty Crash", "Dawn Taken", "Cosmosiris",
        "Chromolith", "By-your-by", "Brightspar", "Black Swan", "Big Troope", "Babylonian", "Astene Key",
        "Prince of Orion", "Novus Alexander", "New Persean Sun", "Final Authority", "Eternal Loyalty", "Vesta-Qi",
        "Psi-Duke", "Pi-Rover", "Nu-Rusty", "Mu-Buddy", "Mem-Zero", "Hadhoyosh", "Gandareva", "Sword of Andrada",
        "Spear of Destiny", "Reign of Andrada", "Pride of Sindria", "Iron Superiority", "Immortal Emperor",
        "Emperor of Salus", "Andrada Invictus", "Virtue", "Vengeance of Opis", "Spirit of Sindria",
        "Sindria's Revenge", "Realm's Protector", "Prince of Perseus", "Primis Prepotence", "King of Andromeda",
        "Dominance of Will", "Battle of Sindria", "Ascendant Command", "Allegiant Ultimus", "Forbearance",
        "Divine Will", "Bristlecone", "Voidclipper", "Ultramancer", "Star Blazer", "Singularian", "Shuku-Nyora",
        "Oldensilent", "Necromancer", "Monkey Tank", "Miday's Les", "Lotus-eater", "Kappa-Daisy", "Iridescence",
        "In Calizard", "Hypermarine", "Hot To Trot", "Hot Pursuit", "Golden Glow", "Fumbleglory", "Free Evenus",
        "Fated Death", "Convectorea", "Come Ending", "Brightwitch", "Bright Tide", "Blue Sphinx", "Black Agata",
        "Aeon Raptor", "Amber", "Andrada's Conquest", "Tycoon", "Avarus", "Two Pleas", "Purgatory", "Damnation",
        "Vigilant", "Upholder", "Untiring", "Unshaken", "Tireless", "Taciturn", "Augustus", "Alliance", "Hector",
        "Word of Ludd", "Wages Of Sin", "Tree of Life", "Starry Larch", "Sacred March", "Joy in Faith",
        "Gift of Ludd", "Dust To Dust", "Cast-out Sin", "Blessed Muir", "Martyaxwar", "Waw-Romeo", "Tau-Queen",
        "Sade-Toby", "Rikard-Ba", "Kaph-Zulu", "Ismael-Wu", "Hector-Yi", "Eta-Daisy", "Delta-Max",
        "Protector of Askonia", "Judgement of Andrada", "Ascension of Destiny", "Yodh-Windian", "Wrath Aponos",
        "Waking Dream", "Terpsichoria", "Silo Sixteen", "Rid And Gone", "Psycheta-Rus", "Old Forgoyle",
        "Nephi-Prizon", "Naminal Quan", "Manti-Irtius", "Lucky Dragon", "Lord Scarlet", "Lonely Rover",
        "Hurry-scurry", "Highest High", "Haring Shiff", "Good Sunrise", "Golden Arrow", "Go Home Ezra",
        "Give It A Go", "Fusion Flash", "Delta-Dusker", "Dawn Trekker", "Cold Blossom", "At Long Last",
        "Astop Untire", "Is And All", "Dawn of the Final Era", "Sovereign of the Stars", "Reign of the Conqueror",
        "Tears of Ludd", "Hope in Faith", "Heaven's Gate", "Away Our Fear", "Urance Raphie", "Sable-Cherjar",
        "Kamiture Anos", "Ionian Regent", "His Timelense", "Heart of Gold", "Fateful Deeds", "Don't Mind It",
        "Conny's Heart", "Come And Gone", "Aquilon-Echet", "Aim To Please", "Adeon Airtune", "Flagellant",
        "Boramander", "Armageddon", "Apocalypse", "Victory of the Potentate", "Helen", "Osiris", "Hathor", "Anubis",
        "Fulad-zereh", "Azhi Dahaka", "Weeping Willow", "Mother Sequoia", "Cherished Duty", "All Are Mortal",
        "Zeta-Molly", "Taw-Maggie", "Res-Sophie", "Qoph-Bella", "Iota-Rocky", "Fontane-Si", "Florus-Jiu",
        "Erroll-Shi", "Chi-Prince",
        // variant display names
        "Bomber", "Type 301", "Type 300", "FTR Support", "Type 302", "EMP Hardened", "Star Fortress", "Exploration",
        "Mercenary",
        // weapon ids
        "tpc"
    )

    // Tier 1 (5462 bytes): expected saving per byte 0.02 .. 0.03
    private val tier1: List<String> = listOf(
        // faction ids
        "scavengers", "neutral", "remnant",
        // fleet type names
        "Trader", "Enforcer", "Scavenger", "Privateer",
        // fleet type ids
        "trade",
        // hull ids
        "station3", "onslaught_mk1", "module_armour5", "module_armour4", "module_armour3", "module_armour2",
        "module_armour1", "remnant_shield2", "remnant_shield1", "remnant_armour3", "remnant_armour2",
        "remnant_armour1", "module_blocker2", "module_blocker1", "station3_midline", "station2_midline",
        "station1_midline", "module_blocker2c", "module_blocker2b", "station3_hightech", "station2_hightech",
        // hullmod ids
        "ecm",
        // memory keys
        "\$cfai_makeAlwaysPursue", "\$cfai_makeAllowDisengage", "\$ignorePlayerCommRequests",
        "\$core_fleetNotChasingGhost", "\$noShipRecovery", "\$military", "\$core_fleetChasingGhost",
        "\$core_fleetSpecialAction", "\$fidConifgGen", "\$cfai_ignoredByOtherFleets", "\$fidConifg", "\$cfai_noJump",
        "\$playerHostileTimeout", "\$patrolAllowTOff", "\$recentlyRaided", "\$doNotGetSidetracked", "\$isSmuggler",
        "\$pursuePlayer", "\$sawPlayerTransponderOff", "\$noRepImpact", "\$cfai_makeNonHostile",
        "\$cfai_longPursuit", "\$isTradeFleet",
        // person names
        "Benjamin", "Alastair", "Magnetic", "Raheleh", "Juniper", "Xerxes", "Ares", "Poseidon", "Hercules",
        "Arcturus", "Petra", "Valentine", "Frederick", "Christian", "Alexander", "Sedna", "Orion", "Orcus", "Athena",
        "Gilgamesh", "Supreme", "Radiant", "Optimum", "Optimal", "Melodic", "Exalted", "Endless", "Anastasia",
        "Ultimate", "Terminal", "Palatial", "Infinite", "Glorious", "Ethereal", "Korshid", "Wednesday", "Gula",
        "Varuna", "Uranus", "Velocious", "Unequaled", "Sagacious", "Paramount", "Draconian", "Boundless",
        "Ascendant", "Boudicca", "Wintermute", "Prevailing", "Palatinate", "Intangible", "Ghostwheel",
        // portraits
        "graphics/portraits/characters/old_spacer02.png", "graphics/portraits/characters/old_spacer01.png",
        "graphics/portraits/portrait_hegemony06.png", "graphics/portraits/portrait47.png",
        // ship names
        "Vanquilon-Echo", "Spanish Dancer", "So Far So Good", "Shift Enduring", "Rose Celestial", "Raichrome Away",
        "Patorious Jump", "Midgardian Cog", "Hyperparabolic", "His Eyes Black", "Gravity Rental", "Crowning Glory",
        "Cosmic Monarch", "Come Celessity", "Azhi Damaeters", "Arkady Sent Me", "Venturous", "Vengeance",
        "Venerable", "Undaunted", "Trenchant", "Endurance", "Dauntless", "Audacious", "Advantage", "Admirable",
        "Turn of Seasons", "Selfless Giving", "Paradise Awaits", "Miracle of Life", "Memory of Light",
        "Gentle Shepherd", "Away Our Sorrow", "Azhi Sruvara", "Holy Battle", "Sparadise Thach", "Rome Eventually",
        "Prosperous Gale", "Oph-Bellar Rock", "One Blahcalypse", "Lorimier's Hand", "Laputan Machine",
        "Dedekind Cutter", "Chandragone Per", "Amoral Quandary", "Onward Faithful!", "Miriam's Reprise",
        "Ludd's Left Shoe", "Infinite Sublime", "Forget Not Earth", "Thunder-Liu", "Theta-Rusty", "Teth-Sierra",
        "Pistol-Ling", "Lamedh-Bear", "Lambda-Jake", "Gimel-Lucky", "Ayin-Buster", "Aleph-Angel", "Aladdin-San",
        "Ventir Traviolet", "The Ashes Remain", "Scudding Swiftly", "Plutonium Mother", "Mercurial Scythe",
        "Lucky Stroopedux", "Ideal Parameters", "Flatlander Woman", "Faraway And Gone", "Branising Laserq",
        "Basilon-Dukemona", "Babylonian Raven", "All Of Our Hopes", "Sleep Not Forever", "Salvation In Duty",
        "Ludd's Right Shoe", "Hymn Of Old Earth", "Magnate", "Crassus", "Wrath of God", "Chastisement",
        "All Is Taken", "All Is Given", "Worshift Endurang", "Third Questioning", "Stark Intangibles",
        "Moskva-Kassiopeya", "Impartial Eclipse", "Immaculate Vessel", "Hello And Goodbye", "Heart of Darkness",
        "Forever Wandering", "Concharaken Glory", "Vindicator", "Victorious", "Vanquisher", "Unrivalled",
        "Inevitable", "Enterprise", "Allegiance", "If Not Refuge", "Spirit of Devotion", "Cherished Devotion",
        "Waw-Rocky Devengen", "Upsi-Dustey Takard", "Unity of Multitude", "This Time For Real", "Not A Peep Of This",
        "Fortuitous Voyager", "Downcast Ascension", "Don't Mind If I Do", "A Lot To Live Down", "Yodh-Whiskey",
        "Sigma-Prince", "Rho-Princess", "Kappa-Dusker", "Epsilon-Lucy", "Beth-Winston", "Rightness of Action",
        "Righteous Necessity", "Penitence Of Labour", "Blest The Righteous", "Of Many of One", "Look, Unchosen",
        "Ultraviolet Sunrise", "Silent Annunciation", "Calertemper Goddard", "Wrath of Ludd", "Sacrifice-all",
        "Crusadeon Rem", "Worshipful Obedience", "Perdition Awaits Sin", "Paradise Is Promised",
        "Day Of Thanks Giving", "There And Back Again", "Hermanichi-Rockettte", "Frequently Modulated",
        "Flowering of Creation", "Illustrious", "Zayin-Uniform", "Samekh-Cooper", "Omnicron-Echo", "Heth-November",
        "Daleth-Tucker", "Ulysses", "Silent Question", "Be Fulsome Void", "You Found Me Now What", "Servant Of War",
        "Hammer of Ludd", "Burnt Offering", "Not Great Not Terrible", "Loyal To The Seventeen",
        "Limit One Per Customer", "Go On Take A Good Look", "Failure of Imagination", "Eyes Like Nuclear Fire",
        "Salvation In Submission", "To Morphenemonia", "Last Rains of Sestramor", "Indigo", "Freya",
        "Three Sheets To The Wind", "One Key Detail Forgotten", "Let's Give It Another Go", "Hellfire Awaits",
        "Excommunication", "Cleansing Light", "Umbra's Radiation", "Their Acts Willed", "A Return to Never",
        "Aeneas", "Treasure", "Astorian", "Ambition", "Upsilon-Duchess", "Final Redemption", "Divine Judgement",
        "Damnation-to-sin", "Blood of Martyrs", "My Limb of Thought", "In Undeath-nonlife", "Ever To Apex-Nadir",
        "Thunder Child", "Indefatigable", "Sword of Damocles", "Pitiable Offering", "Lash Of Perdition",
        "Humble Submission", "Shannon", "The Hidden Immanence", "Inferno-for-heresy", "Vanguard", "Minerva",
        "By Living as Becoming", "Torment To Apostasy", "Rewards Of Devotion", "Cast Down The Idols",
        "Asymptote  Convergence", "Cast Out The Sinners", "Nullifier", "Necessity", "Long Haul", "Gold Line",
        "Bountiful", "Big Mover", "Big Break", "Venus", "Ceres", "Laika", "Comet", "Sacrifice", "Trade Wind",
        "Prosperity", "Fortuitous", "Fair Trade", "Expedition", "Serendipity", "Margin Call", "Gilden Good",
        "Free Plenty", "Externality", "Prime Lavish", "Lucky Strike", "In The Black", "Gold Fortune", "Karkadann",
        "Cybele", "Charon", "Risk's Reward", "Bright Future", "Risk Accretion", "Invisible Hand",
        // variant display names
        "Type 201", "Type 200", "Super", "Siege", "Missile", "Shieldbreaker", "Pather", "Combat", "Ancient",
        "Rocket", "Patrol", "Hellbore",
        // weapon ids
        "hil"
    )

    // Tier 2 (5487 bytes): expected saving per byte 0.03 .. 0.08
    private val tier2: List<String> = listOf(
        // ai cores
        "omega_core",
        // faction ids
        "lions_guard", "persean", "knights_of_ludd", "luddic_path", "luddic_church", "hegemony", "sindrian_diktat",
        // fleet type names
        "Task Force", "Prospector", "Detachment", "Fast Picket", "Supply Fleet", "Liner Convoy", "Grand Armada",
        "Salvage Fleet", "Relief Convoy", "Investigators", "Bounty Hunter", "Mercenary Scout", "Luddic Pilgrims",
        "Mercenary Patrol", "Inspection Fleet", "Commerce Raiders", "Sacred Protectors", "Mercenary Company",
        "Mercantile Convoy", "Academy Shuttle Service",
        // fleet type ids
        "raider", "taskForce", "mercScout", "tradeLiner", "mercPatrol", "mercArmada", "supplyFleet", "smallTrader",
        "patrolSmall", "patrolLarge", "personBounty", "patrolMedium", "leagueArmada", "academyFleet",
        "tradeSmuggler", "mercPrivateer", "investigators", "battlestation", "shrinePilgrims", "scavengerSmall",
        "scavengerLarge", "leagueEnforcer", "scavengerMedium", "inspectionFleet", "foodReliefFleet",
        "commerceRaiders", "sacredProtectors", "mercBountyHunter",
        // hull ids
        "station1_hightech", "module_bastion_pd1", "module_large2_lowtech", "module_large1_lowtech",
        "module_hightech_strut", "module_decor_midline2", "module_decor_midline1", "wasp", "claw",
        "module_midline_support", "module_midline_citadel", "module_hightech_shield", "module_hightech_hangar",
        "module_hightech_attack", "module_hightech_armour", "module_hightech_citadel", "module_citadel1_lowtech",
        "module_bastion1_lowtech", "remnant_weapon_platform3", "remnant_weapon_platform2",
        "remnant_weapon_platform1", "module_midline_broadside", "module_onslaught_armor_left",
        "module_onslaught_armor_right", "derelict_mothership_pod_m_left", "derelict_mothership_bow_module",
        "derelict_mothership_sensor_mast", "derelict_mothership_pod_s_right", "derelict_mothership_pod_m_right",
        "station_derelict_survey_mothership", "derelict_mothership_pod_engine_left", "platform1", "station2",
        "station1", "nova", "fury", "station_small", "omen", "dram", "aspect", "remnant_hangar1", "mule", "doom",
        "crig", "remnant_station2", "talon", "spark", "flash", "cobra", "kite", "mora", "apex",
        "module_midline_bastion", "kite_d", "shade", "atlas", "xyphos", "mule_d", "merlon", "hoplon", "dagger",
        // hullmod ids
        "bdeck",
        // memory keys
        "\$objectiveNonFunctional", "\$isPatrol", "\$salvageSpecialData", "\$cfai_makeHostileWhileTOff",
        "\$lowRepImpact", "\$isPirate", "\$missionImportant", "\$salvageSpecId", "\$cfai_ignoreOtherFleets",
        "\$salvageSeed", "\$sourceMarket", "\$cfai_makeAggressive",
        // person names
        "Unthinkable", "Superlative", "Splendorous", "Resplendent", "Inscrutable", "Titania", "Transcendent", "Heka",
        "Hebe", "Kyugu", "Olympus", "Elysium", "Titan", "Allat",
        // portraits
        "graphics/portraits/portrait38.png", "graphics/portraits/portrait_league14.png",
        "graphics/portraits/portrait_league13.png", "graphics/portraits/portrait_league12.png",
        "graphics/portraits/portrait_league11.png", "graphics/portraits/portrait_league10.png",
        "graphics/portraits/portrait_league09.png", "graphics/portraits/portrait_league08.png",
        "graphics/portraits/portrait_league07.png", "graphics/portraits/portrait_league06.png",
        "graphics/portraits/portrait_league05.png", "graphics/portraits/portrait_league04.png",
        "graphics/portraits/portrait_league03.png", "graphics/portraits/portrait_league02.png",
        "graphics/portraits/portrait_league01.png", "graphics/portraits/portrait_league00.png",
        "graphics/portraits/portrait_defactioned07.png", "graphics/portraits/portrait_defactioned06.png",
        "graphics/portraits/portrait_defactioned05.png", "graphics/portraits/portrait_defactioned04.png",
        "graphics/portraits/portrait_defactioned03.png", "graphics/portraits/portrait_defactioned02.png",
        "graphics/portraits/portrait_defactioned01.png", "graphics/portraits/portrait_defactioned00.png",
        "graphics/portraits/portrait46.png", "graphics/portraits/portrait45.png",
        "graphics/portraits/portrait44.png", "graphics/portraits/portrait43.png",
        "graphics/portraits/portrait_corporate14.png", "graphics/portraits/portrait_corporate13.png",
        "graphics/portraits/portrait_corporate12.png", "graphics/portraits/portrait_corporate11.png",
        "graphics/portraits/portrait_corporate10.png", "graphics/portraits/portrait_corporate09.png",
        // posts
        "boss", "shady", "fence", "exile", "trader", "novice", "hermit", "hacker", "doctor", "curate",
        // ship names
        "Stellar Profits", "Free Enterprise", "Fortunate Cloud", "Auspicious Jump", "Saturn", "Aurora",
        "Sound Investment", "Halcyon On And On", "Abundant Returning", "Zenith", "Meteor", "Coeurl", "Ultraviolet",
        "Neptune", "Mercury", "Jupiter", "Hesperus", "Azimyth", "Azimuth", "Starhawk", "Solstice", "Sol Long",
        "Skyspark", "Redshift", "Ion Rose", "Cosmosis", "Tenacious", "Taikonaut", "Starflyer", "Starfarer",
        "Space Dog", "Red Dwarf", "Mercurial", "Kosmoskva", "Kosmonaut", "High Moon", "Bon Comet", "Blueshift",
        "Blue Star", "Black Sun", "Phobos", "Deimos", "Stellarian", "Starfinder", "Stardriven", "Star Witch",
        "Spacefarer", "Solar Wind", "Nightfarer", "Green Moon", "Fair Orbit", "Venusian", "Via Acubens",
        "Stars Below", "Rigel Flyer", "Nightrunner", "Lightspeed+", "Lalande Ho!", "Gegenschein", "Caspian Sky",
        "Bright Burn", "Stellar Wind", "Stellar Rose", "Space Raptor", "Kardashevian", "Dark Matters",
        "Comet's Tail", "Caloris Born", "Imbrium Flyer", "Fleeting Moon", "Keter", "Ishim", "Zodiacal Light",
        "Tachyon And On", "Sirius Express", "Ever Starbound", "Chatelain Thea", "Antares Bright", "Orphan of Earth",
        "Memory of Deneb", "Gagarin's Dream", "Forgive Us Enos", "Cygnus Gleaming", "Aloft Parabolic",
        "7000 Lightyears", "Upon Starry Road", "Queen of Jupiter", "Plentiful Volume", "Mu Herculis Lost",
        "Korolev Dreaming", "Einstein's Demon", "Celestial Breeze", "Aldebaran Rising", "10000 Lightyears",
        "Pride of Achernar", "Polaris Traveller", "Old Earth Or Bust", "Interstellaration", "Interstellar Rain",
        "Third Moon Of Mars", "Laniakea Traveller", "Glory of the Stars", "Don't Stop Until C", "Arising Hyperbolic",
        "Prize of High Orbit", "Prince of the Stars", "Interstitial Breeze", "Hyperspace Dreaming",
        "Tsiolkovsky's Vision", "Apologies To Goddard", "Never Again Chi Pegasi",
        // variant display names
        "Type 101", "Type 100", "Frontier", "Experimental", "Custom", "Light", "Pirate", "Outdated",
        "Orbital Station", "Smuggler", "Type 450", "Type 350", "Type 250", "Defense"
    )

    // Tier 3 (5438 bytes): expected saving per byte 0.08 .. 0.13
    private val tier3: List<String> = listOf(
        // faction ids
        "tritachyon", "independent", "pirates", "player", "mercenary",
        // hull ids
        "facet", "warthog", "trident", "thunder", "sarissa", "ravelin", "piranha", "longbow", "gladius", "eagle_d",
        "dem_drone", "eagle", "atlas2", "anubis", "gemini", "lumen", "hound", "shuttlepod", "tarsus_d", "hermes_d",
        "gargoyle", "falcon_d", "drone_pd", "tarsus", "wolf", "nebula", "shrike", "scarab", "apogee", "perdition",
        "mercury_d", "buffalo_d", "hermes", "heron", "rampart", "phantom", "odyssey", "hound_d", "gremlin", "wolf_d",
        "warden", "sentry", "picket", "revenant", "cerberus_d", "broadsword", "phaeton", "falcon", "drover",
        "condor", "astral", "talon_tritachyon", "manticore_pather", "drone_borer", "dominator_d", "buffalo",
        "valkyrie", "sunder_d", "lasher_d", "falcon_p", "pegasus", "grendel", "fulgent", "mining_drone",
        "drone_sensor", "colossus", "sunder", "legion", "monitor", "drone_assault", "venture_p", "harbinger",
        // hullmod ids
        "rugged", "coherer", "vastbulk", "delicate", "civgrade",
        // memory keys
        "\$core_fleetBusy", "\$cfai_makeHostile",
        // person names
        "Oberon", "Nessus", "Hektor", "Cabrakan", "Dysnomia", "Eulalia", "Elektra", "Hygiea", "Kleopatra",
        // portraits
        "graphics/portraits/portrait_corporate08.png", "graphics/portraits/portrait_corporate07.png",
        "graphics/portraits/portrait_corporate06.png", "graphics/portraits/portrait_corporate05.png",
        "graphics/portraits/portrait_corporate04.png", "graphics/portraits/portrait_corporate03.png",
        "graphics/portraits/portrait_corporate02.png", "graphics/portraits/portrait_corporate01.png",
        "graphics/portraits/portrait_pirate22.png", "graphics/portraits/portrait_pirate21.png",
        "graphics/portraits/portrait_pirate20.png", "graphics/portraits/portrait_pirate19.png",
        "graphics/portraits/portrait_pirate18.png", "graphics/portraits/portrait_pirate17.png",
        "graphics/portraits/portrait_pirate16.png", "graphics/portraits/portrait_pirate15.png",
        "graphics/portraits/portrait_pirate14.png", "graphics/portraits/portrait_pirate13.png",
        "graphics/portraits/portrait_pirate12.png", "graphics/portraits/portrait_pirate11.png",
        "graphics/portraits/portrait_pirate10.png", "graphics/portraits/portrait_pirate09.png",
        "graphics/portraits/portrait_pirate08.png", "graphics/portraits/portrait_pirate07.png",
        "graphics/portraits/portrait_pirate06.png", "graphics/portraits/portrait_pirate05.png",
        "graphics/portraits/portrait_pirate04.png", "graphics/portraits/portrait_pirate03.png",
        "graphics/portraits/portrait_pirate02.png", "graphics/portraits/portrait_pirate01.png",
        "graphics/portraits/portrait_hegemony17.png", "graphics/portraits/portrait_hegemony16.png",
        "graphics/portraits/portrait_hegemony15.png", "graphics/portraits/portrait_hegemony14.png",
        "graphics/portraits/portrait_hegemony13.png", "graphics/portraits/portrait_hegemony12.png",
        "graphics/portraits/portrait_hegemony11.png", "graphics/portraits/portrait_hegemony10.png",
        "graphics/portraits/portrait_hegemony09.png", "graphics/portraits/portrait_hegemony08.png",
        "graphics/portraits/portrait_hegemony07.png", "graphics/portraits/portrait_hegemony05.png",
        "graphics/portraits/portrait_hegemony04.png", "graphics/portraits/portrait_hegemony01.png",
        "graphics/portraits/portrait23.png", "graphics/portraits/portrait_luddic16.png",
        "graphics/portraits/portrait_luddic15.png", "graphics/portraits/portrait_luddic14.png",
        "graphics/portraits/portrait_luddic13.png", "graphics/portraits/portrait_luddic12.png",
        "graphics/portraits/portrait_hegemony03.png", "graphics/portraits/portrait_hegemony02.png",
        "graphics/portraits/portrait_mercenary07.png", "graphics/portraits/portrait48.png",
        "graphics/portraits/portrait31.png", "graphics/portraits/portrait14.png",
        "graphics/portraits/portrait_diktat16.png", "graphics/portraits/portrait_diktat15.png",
        "graphics/portraits/portrait_diktat14.png", "graphics/portraits/portrait_diktat13.png",
        "graphics/portraits/portrait_diktat12.png", "graphics/portraits/portrait_diktat11.png",
        "graphics/portraits/portrait_diktat10.png", "graphics/portraits/portrait_diktat09.png",
        "graphics/portraits/portrait_luddic11.png", "graphics/portraits/portrait_luddic10.png",
        "graphics/portraits/portrait_luddic09.png", "graphics/portraits/portrait_luddic08.png",
        "graphics/portraits/portrait_luddic07.png", "graphics/portraits/portrait_luddic06.png",
        "graphics/portraits/portrait_luddic05.png", "graphics/portraits/portrait_luddic04.png",
        "graphics/portraits/portrait_luddic03.png", "graphics/portraits/portrait_luddic02.png",
        "graphics/portraits/portrait_luddic01.png", "graphics/portraits/portrait_luddic00.png",
        // posts
        "warlord", "provost", "pilgrim", "heretic", "smuggler", "merchant", "investor", "initiate", "gangster",
        "crewBoss", "betaCore", "activist", "subcurate", "pensioner", "gammaCore", "freeAdmin", "alphaCore",
        "portmaster", "armsDealer", "archcurate", "guildMaster", "guardLeader", "academician", "shrinePriest",
        "investigator", "entrepreneur", "supplyOfficer", "supplyManager", "securityChief", "minorCriminal",
        "communeLeader", "baseCommander", "administrator", "underworldBoss", "synodSubcurate", "excubitorOrbis",
        "medicalSupplier", "genericMilitary",
        // ranks
        "pilot", "elder", "clone", "sister", "mother", "father",
        // ship names
        "Queen of the Main Sequence", "Bright Spark", "Triton", "Thisbe", "Themis", "Psyche", "Europa", "Chiron",
        "Hafaza", "Erelim", "Elohim", "Azrael", "Azazel", "Adriel", "Urania", "Pyramus", "Eunomia", "Raphael",
        "Ophanim", "Ganymede", "Callisto", "Star Ark", "Seraphim", "Cherubim", "Hashmallim", "Bene Elohim",
        // ship name prefixes
        "DSS ",
        // weapon slot ids
        "WS0023", "WS0022", "WS0021", "WS0020", "WS0019", "WS0018", "WS0017", "WS0016", "WS0015", "WS0014", "WS0013",
        "WS0012", "WS0011", "WS0010", "WS0009", "WS0008", "WS0007", "WS0006", "WS0005", "WS0004", "WS0003", "WS0002",
        "WS0001", "WS0000",
        // variant display names
        "Shielded", "CGR Standard", "Escort", "Battlestation", "Point Defense",
        // weapon ids
        "novalaser", "lidardish", "sensordish", "minelayer2", "minelayer1", "clusterbomb", "blinker_red",
        "motelauncher", "lights_hound", "fragbomb", "hydra_payload", "gazer_payload", "canister_flak",
        "blinker_green"
    )

    // Tier 4 (5455 bytes): expected saving per byte 0.13 .. 0.26
    private val tier4: List<String> = listOf(
        // hull ids
        "eagle_xiv", "colossus3", "colossus2", "berserker", "bastillon", "glimmer", "gryphon", "shrouded_vortex",
        "shrouded_ejecta", "ziggurat", "wayfarer", "invictus", "executor", "venture", "starliner", "tesseract",
        "drone_terminator", "drone_pd_midline", "prometheus", "enforcer_d", "shepherd", "tempest", "hyperion",
        "defender", "shard_right", "paragon", "retribution", "prometheus2", "sunder_LG", "scintilla", "falcon_LG",
        "centurion", "afflictor", "eagle_LG", "conquest", "medusa", "kite_original", "hammerhead_d", "enforcer_xiv",
        "centurion_LG", "hive_unit", "legion_xiv", "falcon_xiv", "buffalo2", "cerberus", "onslaught_xiv",
        "dominator_xiv", "radiant", "attack_swarm", "module_hightech_attack_minelayer", "onslaught",
        "venture_pather", "shrike_pirates", "vanguard", "mercury", "brawler", "aurora", "brawler_LG",
        "shade_d_pirates", "mudskipper", "assault_unit", "brilliant", "shard_left", "buffalo_pirates",
        "gremlin_d_pirates", "enforcer_d_pirates", "hammerhead_LG", "buffalo_hegemony", "manticore",
        "gremlin_luddic_path", "afflictor_d_pirates", "buffalo_tritachyon", "shrouded_maw", "shrouded_eye",
        "buffalo_luddic_church", "overseer_unit", "lasher_pather", "champion", "vanguard_pirates", "dominator",
        "wolf_d_pirates", "brawler_pather", "manticore_pirates", "standoff_unit", "mudskipper2",
        "eradicator_pirates", "fabricator_unit", "lasher", "shrouded_tendril", "skirmish_unit", "kite_pirates",
        "shrouded_maelstrom", "brawler_tritachyon", "mule_d_pirates", "lasher_luddic_church", "hammerhead",
        "hound_hegemony", "wolf_hegemony", "eradicator", "kite_hegemony", "hound_luddic_path", "vigilance",
        "hound_luddic_church",
        // hullmod ids
        "tow_cable", "fluxshunt", "phasefield", "fourteenth", "vast_hangar", "advancedcore", "supercomputer",
        "shard_spawner", "repair_gantry", "converted_bay", "axialrotation", "threat_hullmod", "shielded_holds",
        "never_detaches", "missile_reload", "manual_systems", "ground_support", "ex_phase_coils", "anchorrotation",
        "ablative_armor", "low_maintenance", "heg_militarized", "dweller_hullmod", "do_not_back_off",
        "chassis_storage", "always_detaches", "shield_always_on", "shared_flux_sink", "high_maintenance",
        "stealth_minefield", "reduced_explosion", "design_compromises", "do_not_fire_through",
        "stealth_minefield_lt", "drive_field_stabilizer", "advanced_ground_support", "distributed_fire_control",
        "automated", "terminator_core",
        // portraits
        "graphics/portraits/portrait40.png", "graphics/portraits/portrait39.png", "graphics/portraits/threat.png",
        "graphics/portraits/portrait41.png", "graphics/portraits/characters/omega.png",
        "graphics/portraits/characters/dwelller.png", "graphics/portraits/portrait35.png",
        "graphics/portraits/portrait32.png", "graphics/portraits/portrait30.png",
        "graphics/portraits/portrait26.png", "graphics/portraits/portrait25.png",
        "graphics/portraits/portrait_mercenary05.png", "graphics/portraits/portrait13.png",
        "graphics/portraits/portrait_mercenary06.png", "graphics/portraits/portrait_diktat08.png",
        "graphics/portraits/portrait_diktat07.png", "graphics/portraits/portrait_diktat06.png",
        "graphics/portraits/portrait_diktat05.png", "graphics/portraits/portrait_diktat04.png",
        "graphics/portraits/portrait_diktat03.png", "graphics/portraits/portrait_diktat02.png",
        "graphics/portraits/portrait_diktat01.png", "graphics/portraits/portrait36.png",
        "graphics/portraits/portrait27.png", "graphics/portraits/portrait17.png",
        "graphics/portraits/portrait12.png", "graphics/portraits/portrait42.png",
        "graphics/portraits/portrait_mercenary04.png", "graphics/portraits/portrait34.png",
        "graphics/portraits/portrait33.png", "graphics/portraits/portrait21.png",
        "graphics/portraits/portrait22.png", "graphics/portraits/portrait_mercenary02.png",
        "graphics/portraits/portrait_mercenary01.png", "graphics/portraits/portrait_mercenary08.png",
        "graphics/portraits/portrait_ai3b.png", "graphics/portraits/portrait_ai2b.png",
        "graphics/portraits/portrait37.png", "graphics/portraits/portrait28.png",
        "graphics/portraits/portrait29.png", "graphics/portraits/portrait20.png",
        "graphics/portraits/portrait15.png",
        // posts
        "stationCommander", "personnelOfficer", "outpostCommander", "officer_for_hire", "marineSquadLeader",
        "commodities_agent", "nanoforge_engineer", "intelligenceDirector", "militaryAdministrator",
        "associate_academician", "assistant_academician",
        // ranks
        "spaceEnsign", "agent", "brother", "fugitive", "spaceChief", "spaceSailor", "houseLeader", "groundMajor",
        "knightCaptain", "groundPrivate", "groundGeneral", "groundColonel", "groundCaptain", "unknown", "citizen",
        "groundSergeant", "freedomFighter", "houseLeaderMale", "groundLieutenant", "houseLeaderFemale",
        "deputyStarMarshal", "chiefHighInspector", "criminal", "artificialIntelligence", "guardHighDeputyExecutor",
        // ship names
        "Hayot Ha Kodesh", "Unnamed", "Nameless",
        // ship name prefixes
        "RSDS ", "01011001 ", "LSS ", "LGS ",
        // skill ids
        "sensors", "salvaging", "omega_ecm", "navigation", "neural_link", "weapon_drills", "field_repairs",
        "bulk_transport", "automated_ships", "aptitude_combat", "space_operations", "hull_restoration",
        "colony_management",
        // variant display names
        "Overdriven", "Interceptor", "Fire Support", "Balanced", "Raider", "Elite",
        // weapon ids
        "pusherplate_lt", "lights_buffalo", "gorgon_payload", "flarelauncher3", "flarelauncher2", "flarelauncher1",
        "dragon_payload", "targetinglaser3", "targetinglaser2", "targetinglaser1", "pdburst_fighter",
        "od_bomblauncher", "nb_bomblauncher", "motelauncher_hf", "interdictorbeam", "flarelauncher21",
        "pusherplate_nova", "terminator_missile", "riftbeam_minelayer", "riftlance_minelayer", "lightmortar_fighter",
        "pusherplate_lt_small", "riftcascade_minelayer", "bomb", "rift_lightning_minelayer", "sabot_fighter",
        "phasecl_bomber", "swarmer_fighter", "irpulse_fighter", "ioncannon_fighter", "heavy_adjudicator",
        "annihilator_fighter",
        // fighter wing ids
        "borer_wing", "sarissa_wing", "terminator_wing", "mining_drone_wing", "attack_swarm_wing",
        "aspect_shock_wing", "aspect_attack_wing", "aspect_missile_wing", "shrouded_vortex_wing",
        "shrouded_ejecta_wing", "reclamation_swarm_wing", "construction_swarm_wing", "wasp_wing",
        "aspect_shieldbreaker_wing", "lux_wing", "cobra_wing"
    )

    // Tier 5 (5461 bytes): expected saving per byte 0.26 .. 1.70
    private val tier5: List<String> = listOf(
        // ai cores
        "alpha_core", "beta_core", "gamma_core",
        // hull ids
        "enforcer", "cerberus_luddic_path", "kite_luddic_path", "hound_d_pirates", "cerberus_d_pirates", "guardian",
        // hullmod ids
        "eccm", "high_frequency_attractor", "no_weapon_flux", "comp_hull", "comp_armor", "ill_advised",
        "faulty_auto", "damaged_deck", "comp_storage", "andrada_mods", "unstable_coils", "damaged_mounts",
        "faulty_grid", "glitched_sensors", "erratic_injector", "destroyed_mounts", "degraded_shields",
        "fragile_subsystems", "malfunctioning_comms", "degraded_drive_field", "comp_structure",
        "increased_maintenance", "degraded_life_support", "defective_manufactory", "nav_relay", "degraded_engines",
        "hiressensors", "autorepair", "phase_anchor", "frontemitter", "pdintegration", "frontshield",
        "escort_package", "shrouded_lens", "assault_package", "adaptiveshields", "shield_shunt", "adaptive_coils",
        "neural_interface", "high_scatter_amp", "shrouded_mantle", "fluxcoil", "neural_integrator",
        "solar_shielding", "surveying_equipment", "additional_berthing", "recovery_shuttles", "shrouded_thunderhead",
        "expanded_cargo_holds", "converted_fighterbay", "augmentedengines", "auxiliary_fuel_tanks",
        "operations_center", "pointdefenseai", "turretgyros", "efficiency_overhaul", "defensive_targeting_array",
        "secondary_fabricator", "fragment_coordinator", "fragment_swarm", "advancedshieldemitter",
        "expanded_deck_crew", "extendedshieldemitter", "reinforcedhull", "militarized_subsystems",
        "converted_hangar", "safetyoverrides", "missile_autoloader", "magazines", "blast_doors",
        "ballistic_rangefinder", "heavyarmor", "advancedoptics", "unstable_injector", "insulatedengine",
        "stabilizedshieldemitter", "hardened_subsystems", "fluxbreakers", "armoredweapons", "missleracks",
        "auxiliarythrusters", "fluxdistributor", "dedicated_targeting_core", "targetingunit",
        "hardenedshieldemitter",
        // person memory keys
        "voice",
        // personalities
        "timid", "reckless", "steady", "cautious", "aggressive",
        // portraits
        "graphics/portraits/portrait_mercenary03.png", "graphics/portraits/portrait18.png",
        "graphics/portraits/portrait16.png", "graphics/portraits/portrait_ai1b.png",
        // posts
        "officer", "patrolCommander", "fleetCommander",
        // ranks
        "terrorist", "executive", "aristocrat", "specialAgent", "factionLeader", "seniorExecutive",
        "spaceLieutenant", "spaceCaptain", "spaceCommander", "spaceAdmiral",
        // ship name prefixes
        "SDS ", "PLS ", "CGR ", "TTS ", "HSS ", "TTDS ", "ISS ",
        // skill ids
        "auxiliary_support", "aptitude_industry", "makeshift_equipment", "aptitude_technology",
        "aptitude_leadership", "planetary_operations", "special_modifications", "containment_procedures",
        "phase_corps", "crew_training", "carrier_group", "hypercognition", "fighter_uplink", "tactical_drills",
        "flux_regulation", "wolfpack_tactics", "support_doctrine", "officer_training", "best_of_the_best",
        "officer_management", "electronic_warfare", "industrial_planning", "force_concentration",
        "derelict_contingent", "coordinated_maneuvers", "cybernetic_augmentation", "helmsmanship", "point_defense",
        "phase_mastery", "damage_control", "target_analysis", "polarized_armor", "ordnance_expert",
        "strike_commander", "gunnery_implants", "field_modulation", "combat_endurance", "systems_expertise",
        "shield_modulation", "impact_mitigation", "ballistic_mastery", "ranged_specialization",
        "energy_weapon_mastery", "missile_specialization", "reliability_engineering",
        // weapon slot ids
        "WS 040", "WS 039", "WS 038", "WS 037", "WS 036", "WS 035", "WS 034", "WS 033", "WS 030", "WS 027", "WS 025",
        "WS 032", "WS 031", "WS 029", "WS 028", "WS 026", "WS 024", "WS 022", "WS 023", "WS 021", "WS 019", "WS 020",
        "WS 017", "WS 018", "WS 016", "WS 015", "WS 014", "WS 013", "WS 012", "WS 011", "WS 010", "WS 000", "WS 009",
        "WS 008", "WS 007", "WS 006", "WS 005", "WS 004", "WS 003", "WS 002", "WS 001",
        // variant display names
        "Support", "Close Support", "Strike", "Attack", "Assault", "Standard",
        // variant tags
        "fx_drone", "unboardable", "unrestorable", "no_ai_core_drop", "always_recoverable",
        "consistent_weapon_drops", "variant_allow_excess_op_etc", "variant_always_retain_smods_on_salvage",
        // voices
        "pather", "aristo", "spacer", "villain", "soldier", "official", "faithful", "business", "scientist",
        // weapon ids
        "hydra", "dragon", "gauss", "gazer", "gorgon", "flak", "locust", "amsrm", "vpdriver", "phasecl", "cyclone",
        "gorgonpod", "plasma", "hephag", "pilum", "mark9", "gazerpod", "cryoflux", "squall", "sabot_single",
        "atropos", "rifttorpedo", "riftcascade", "pilum_large", "mjolnir", "jackhammer", "heavyburst",
        "hammer_single", "riftbeam", "breach", "hammerrack", "gigacannon", "hellbore", "dragonpod", "sabot",
        "heavymg", "swarm_launcher", "realitydisruptor", "voidblaster", "riftlance", "hurricane", "rift_lightning",
        "multineedler", "salamanderpod", "abyssal_glare", "railgun", "ionbeam", "mininglaser", "cryoblaster",
        "vortex_launcher", "devouring_swarm", "shredder", "hammer", "chaingun", "voltaic_cannon", "harpoon_single",
        "atropos_single", "swarmer", "pseudoparticle_jet", "breachpod", "amblaster", "neutron_torpedo",
        "assaying_rift", "reaper", "phasebeam", "dualflak", "neoferric_quadcoil", "typhoon", "heavyblaster",
        "autopulse", "miningblaster", "seeker_fragment", "irautolance", "lightag", "irpulse", "ionpulser",
        "unstable_fragment", "harpoonpod", "disintegrator", "devastator", "resonatormrm", "heavymortar",
        "heavymauler", "kineticblaster", "lightac", "heavyac", "minipulser", "heavy_mass_driver", "sabotpod",
        "arbalest", "lrpdlaser", "kinetic_fragments", "lightmortar", "harpoon", "tachyonlance", "taclaser",
        "lightmg", "pdburst", "lightdualac", "heatseeker", "ioncannon", "heavyneedler", "annihilatorpod",
        "annihilator", "voltaic_discharge", "vulcan", "hveldriver", "pulselaser", "lightneedler",
        "inimical_emanation", "light_mass_driver", "lightdualmg", "pdlaser", "shockrepeater", "gravitonbeam",
        // fighter wing ids
        "xyphos_wing", "claw_wing", "spark_wing", "trident_wing", "warthog_wing", "longbow_wing", "gladius_wing",
        "dagger_wing", "talon_wing", "piranha_wing", "perdition_wing", "flash_wing", "thunder_wing", "hoplon_wing",
        "broadsword_wing"
    )

    val v2: ByteArray = build(tier0 + tier1 + tier2 + tier3 + tier4 + tier5)
}