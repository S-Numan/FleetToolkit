package fleetkit.serialization.dictionaries

import fleetkit.serialization.core.ByteBuffers

/**
 * Preset zlib dictionary for serialized VariantAPI / FleetMemberAPI / PersonAPI / CampaignFleetAPI data.
 *
 * Generated from Starsector 0.98.5a-RC4 game data. Every string was ranked by
 *   expected occurrences per 100 serialized fleet members * estimated bits saved per hit / dictionary bytes
 * and the best ones were packed into exactly 32 KB. Tiers below run least valuable -> most valuable, because zlib
 * codes nearby matches in fewer bits, so the hottest strings (slot ids, common weapons/hullmods, ...) sit at the end.
 * Short strings (< 4 chars) were dropped: a match costs about as much as the literal bytes.
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

    // Tier 0 (5468 bytes): expected saving per byte 0.20 .. 0.29
    private val tier0: List<String> = listOf(
        // faction ids
        "poor",
        // hull ids
        "station3",
        // person names
        "Titania", "Transcendent", "Heka", "Hebe", "Kyugu",
        // portraits
        "graphics/portraits/portrait38.png", "graphics/portraits/portrait_league14.png",
        "graphics/portraits/portrait_league13.png", "graphics/portraits/portrait_league12.png",
        "graphics/portraits/portrait_league11.png", "graphics/portraits/portrait_league10.png",
        "graphics/portraits/portrait_league09.png", "graphics/portraits/portrait_league08.png",
        "graphics/portraits/portrait_league07.png", "graphics/portraits/portrait_league06.png",
        "graphics/portraits/portrait_league05.png", "graphics/portraits/portrait_league04.png",
        "graphics/portraits/portrait_league03.png", "graphics/portraits/portrait_league02.png",
        "graphics/portraits/portrait_league01.png", "graphics/portraits/portrait_league00.png",
        // posts
        "boss",
        // ship names
        "Flagellant", "Fulad-zereh", "Azhi Dahaka", "Weeping Willow", "Mother Sequoia", "Cherished Duty",
        "All Are Mortal", "Zeta-Molly", "Taw-Maggie", "Res-Sophie", "Qoph-Bella", "Iota-Rocky", "Fontane-Si",
        "Florus-Jiu", "Erroll-Shi", "Chi-Prince", "Vanquilon-Echo", "Spanish Dancer", "So Far So Good",
        "Shift Enduring", "Rose Celestial", "Raichrome Away", "Patorious Jump", "Midgardian Cog", "Hyperparabolic",
        "His Eyes Black", "Gravity Rental", "Crowning Glory", "Cosmic Monarch", "Come Celessity", "Azhi Damaeters",
        "Arkady Sent Me", "Venturous", "Vengeance", "Venerable", "Undaunted", "Trenchant", "Endurance", "Dauntless",
        "Audacious", "Advantage", "Admirable", "Turn of Seasons", "Selfless Giving", "Paradise Awaits",
        "Miracle of Life", "Memory of Light", "Gentle Shepherd", "Away Our Sorrow", "Azhi Sruvara",
        "Sparadise Thach", "Rome Eventually", "Prosperous Gale", "Oph-Bellar Rock", "One Blahcalypse",
        "Lorimier's Hand", "Laputan Machine", "Dedekind Cutter", "Chandragone Per", "Amoral Quandary", "Holy Battle",
        "Onward Faithful!", "Miriam's Reprise", "Ludd's Left Shoe", "Infinite Sublime", "Forget Not Earth",
        "Thunder-Liu", "Theta-Rusty", "Teth-Sierra", "Pistol-Ling", "Lamedh-Bear", "Lambda-Jake", "Gimel-Lucky",
        "Ayin-Buster", "Aleph-Angel", "Aladdin-San", "Ventir Traviolet", "The Ashes Remain", "Scudding Swiftly",
        "Plutonium Mother", "Mercurial Scythe", "Lucky Stroopedux", "Ideal Parameters", "Flatlander Woman",
        "Faraway And Gone", "Branising Laserq", "Basilon-Dukemona", "Babylonian Raven", "All Of Our Hopes",
        "Sleep Not Forever", "Salvation In Duty", "Ludd's Right Shoe", "Hymn Of Old Earth", "Worshift Endurang",
        "Third Questioning", "Stark Intangibles", "Moskva-Kassiopeya", "Impartial Eclipse", "Immaculate Vessel",
        "Hello And Goodbye", "Heart of Darkness", "Forever Wandering", "Concharaken Glory", "Wrath of God",
        "Chastisement", "All Is Taken", "All Is Given", "Magnate", "Crassus", "Vindicator", "Victorious",
        "Vanquisher", "Unrivalled", "Inevitable", "Enterprise", "Allegiance", "If Not Refuge", "Spirit of Devotion",
        "Cherished Devotion", "Saturn", "Aurora", "Waw-Rocky Devengen", "Upsi-Dustey Takard", "Unity of Multitude",
        "This Time For Real", "Not A Peep Of This", "Fortuitous Voyager", "Downcast Ascension", "Don't Mind If I Do",
        "A Lot To Live Down", "Yodh-Whiskey", "Sigma-Prince", "Rho-Princess", "Kappa-Dusker", "Epsilon-Lucy",
        "Beth-Winston", "Rightness of Action", "Righteous Necessity", "Penitence Of Labour", "Blest The Righteous",
        "Ultraviolet Sunrise", "Silent Annunciation", "Calertemper Goddard", "Of Many of One", "Look, Unchosen",
        "Wrath of Ludd", "Sacrifice-all", "Crusadeon Rem", "Worshipful Obedience", "Perdition Awaits Sin",
        "Paradise Is Promised", "Day Of Thanks Giving", "There And Back Again", "Hermanichi-Rockettte",
        "Frequently Modulated", "Flowering of Creation", "Illustrious", "Zayin-Uniform", "Samekh-Cooper",
        "Omnicron-Echo", "Heth-November", "Daleth-Tucker", "Silent Question", "Be Fulsome Void",
        "You Found Me Now What", "Ulysses", "Servant Of War", "Hammer of Ludd", "Burnt Offering",
        "Not Great Not Terrible", "Loyal To The Seventeen", "Limit One Per Customer", "Go On Take A Good Look",
        "Failure of Imagination", "Eyes Like Nuclear Fire", "Salvation In Submission", "Last Rains of Sestramor",
        "To Morphenemonia", "Three Sheets To The Wind", "One Key Detail Forgotten", "Let's Give It Another Go",
        "Hellfire Awaits", "Excommunication", "Cleansing Light", "Sacrifice", "Umbra's Radiation",
        "Their Acts Willed", "A Return to Never", "Upsilon-Duchess", "Treasure", "Astorian", "Ambition",
        "Final Redemption", "Divine Judgement", "Damnation-to-sin", "Blood of Martyrs", "My Limb of Thought",
        "In Undeath-nonlife", "Ever To Apex-Nadir", "Thunder Child", "Indefatigable", "Sword of Damocles",
        "Pitiable Offering", "Lash Of Perdition", "Humble Submission", "The Hidden Immanence", "Hesperus",
        "Inferno-for-heresy", "Vanguard", "By Living as Becoming", "Torment To Apostasy", "Rewards Of Devotion",
        "Cast Down The Idols", "Asymptote  Convergence", "Neptune", "Mercury", "Jupiter", "Cast Out The Sinners",
        "Nullifier", "Necessity", "Long Haul", "Gold Line", "Bountiful", "Big Mover", "Big Break", "Trade Wind",
        "Prosperity", "Fortuitous", "Fair Trade", "Expedition", "Laika", "Comet",
        // stock variant ids
        "venture_Exploration", "hammerhead_Balanced", "wolf_Strike", "mora_Strike", "doom_Strike", "falcon_CS",
        "paragon_Elite", "medusa_Attack", "condor_Strike", "astral_Strike", "astral_Attack", "apex_Standard",
        "standoff_unit_Type301", "standoff_unit_Type300", "standoff_unit_Type302", "sunder_Assault",
        "lasher_Assault", "vanguard_Strike", "vanguard_Attack", "hyperion_Attack", "wolf_hegemony_PD",
        "wolf_hegemony_CS", "venture_Outdated", "venture_Balanced", "radiant_Standard", "champion_Assault",
        "lasher_Overdriven", "nova_Attack", "fury_Attack", "onslaught_Standard", "onslaught_Outdated",
        "eradicator_Assault", "sunder_LG_CS", "kite_Support", "heron_Strike", "heron_Attack", "fury_Support",
        "falcon_LG_CS", "facet_Attack", "nova_Standard", "falcon_Attack", "facet_Attack2", "eagle_Assault",
        "pegasus_Strike", "executor_Elite", "eagle_Balanced", "conquest_Elite", "glimmer_Support", "fulgent_Support",
        "fulgent_Assault",
        // variant display names
        "Type 201", "Type 200", "Super", "Siege", "Missile", "Shieldbreaker", "Pather", "Combat", "Rocket",
        // variant id suffixes
        "_Type201", "_Type200", "_Bomber", "_Missile", "_Shieldbreaker", "_Super"
    )

    // Tier 1 (5457 bytes): expected saving per byte 0.29 .. 0.46
    private val tier1: List<String> = listOf(
        // ai cores
        "omega_core",
        // faction ids
        "omega",
        // hull ids
        "onslaught_mk1", "module_armour5", "module_armour4", "module_armour3", "module_armour2", "module_armour1",
        "remnant_shield2", "remnant_shield1", "remnant_armour3", "remnant_armour2", "remnant_armour1",
        "module_blocker2", "module_blocker1", "station3_midline", "station2_midline", "station1_midline",
        "module_blocker2c", "module_blocker2b", "station3_hightech", "station2_hightech", "station1_hightech",
        "module_bastion_pd1", "module_large2_lowtech", "module_large1_lowtech", "module_hightech_strut",
        "module_decor_midline2", "module_decor_midline1", "module_midline_support", "module_midline_citadel",
        "module_hightech_shield", "module_hightech_hangar", "module_hightech_attack", "module_hightech_armour",
        "module_hightech_citadel", "module_citadel1_lowtech", "module_bastion1_lowtech", "remnant_weapon_platform3",
        "remnant_weapon_platform2", "remnant_weapon_platform1", "module_midline_broadside", "wasp", "claw",
        "module_onslaught_armor_left", "module_onslaught_armor_right", "derelict_mothership_pod_m_left",
        "derelict_mothership_bow_module", "derelict_mothership_sensor_mast", "derelict_mothership_pod_s_right",
        "derelict_mothership_pod_m_right", "station_derelict_survey_mothership",
        "derelict_mothership_pod_engine_left", "platform1",
        // hullmod ids
        "ecm",
        // memory keys
        "\$bcc_script", "\$untrustworthy", "\$deactivatedTS", "\$story_critical", "\$deactivatedTSOrig",
        "\$anchor_starSystem", "\$core_fightToTheLast", "\$autoOpenBuyShipsTab", "\$tempNotAvoidingAbyssal",
        "\$prevSalvageSpecialData", "\$avoidingAbyssalHyperspace", "\$makeNonHostileTakesPriority",
        "\$doNotTryToAvoidNearbyFleets", "\$cfai_neverAvoidPlayerSlowly", "\$marketCanAlwaysIncentivizeGrowth",
        // person names
        "Olympus", "Elysium", "Titan", "Allat",
        // portraits
        "graphics/portraits/portrait_defactioned07.png", "graphics/portraits/portrait_defactioned06.png",
        "graphics/portraits/portrait_defactioned05.png", "graphics/portraits/portrait_defactioned04.png",
        "graphics/portraits/portrait_defactioned03.png", "graphics/portraits/portrait_defactioned02.png",
        "graphics/portraits/portrait_defactioned01.png", "graphics/portraits/portrait_defactioned00.png",
        "graphics/portraits/portrait46.png", "graphics/portraits/portrait45.png",
        "graphics/portraits/portrait44.png", "graphics/portraits/portrait43.png",
        "graphics/portraits/portrait_corporate14.png", "graphics/portraits/portrait_corporate13.png",
        "graphics/portraits/portrait_corporate12.png", "graphics/portraits/portrait_corporate11.png",
        "graphics/portraits/portrait_corporate10.png", "graphics/portraits/portrait_corporate09.png",
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
        // posts
        "shady", "fence", "exile", "trader", "novice", "hermit", "hacker", "doctor", "curate",
        // ship names
        "Ultraviolet", "Serendipity", "Margin Call", "Gilden Good", "Free Plenty", "Externality", "Prime Lavish",
        "Lucky Strike", "In The Black", "Gold Fortune", "Karkadann", "Phobos", "Deimos", "Risk's Reward",
        "Bright Future", "Risk Accretion", "Invisible Hand", "Stellar Profits", "Free Enterprise", "Fortunate Cloud",
        "Auspicious Jump", "Sound Investment", "Halcyon On And On", "Abundant Returning", "Zenith", "Meteor",
        "Coeurl", "Tenacious", "Azimyth", "Azimuth",
        // stock variant ids
        "scintilla_Strike", "pegasus_Balanced", "invictus_Support", "falcon_xiv_Elite", "falcon_LG_Attack",
        "eagle_LG_Support", "eagle_LG_Assault", "sunder_LG_Assault", "scintilla_Support", "invictus_Standard",
        "falcon_xiv_Escort", "executor_Standard", "conquest_Standard", "vigilance_FS", "hammerhead_LG_Elite",
        "assault_unit_Type201", "assault_unit_Type200", "brawler_Elite", "hammerhead_LG_Balanced",
        "vanguard_pirates_Strike", "vanguard_pirates_Attack", "revenant_Elite", "manticore_pirates_Support",
        "manticore_pirates_Assault", "vigilance_Strike", "vigilance_Standard", "shrike_Attack", "grendel_Assault",
        "gryphon_Standard", "wolf_CS", "mudskipper2_CS", "wolf_PD", "lasher_d_CS", "mudskipper2_Hellbore",
        // variant display names
        "Hellbore", "Type 101", "Type 100", "Frontier",
        // variant id suffixes
        "_Fighter_Support", "_Fighter", "_Hellbore", "_Pather", "_Type101", "_Type100", "_Smuggler",
        // weapon ids
        "hil"
    )

    // Tier 2 (5461 bytes): expected saving per byte 0.46 .. 0.75
    private val tier2: List<String> = listOf(
        // faction ids
        "threat", "sleeper", "dweller",
        // fleet type names
        "Trader", "Enforcer", "Scavenger", "Privateer",
        // fleet type ids
        "trade",
        // hull ids
        "station2", "station1", "station_small", "nova", "fury", "remnant_hangar1", "aspect", "omen", "dram",
        "remnant_station2", "mule", "doom", "crig", "talon", "spark", "flash", "cobra", "module_midline_bastion",
        "kite", "kite_d", "mora", "apex", "shade", "atlas",
        // memory keys
        "\$skipTInfo", "\$kantaBluffs", "\$isScavenger", "\$academyFleet", "\$salvageDefProb", "\$cfai_patrolDist",
        // person names
        "Oberon", "Nessus", "Hektor", "Cabrakan", "Dysnomia", "Eulalia", "Elektra", "Hygiea", "Kleopatra",
        // portraits
        "graphics/portraits/portrait23.png", "graphics/portraits/portrait_hegemony03.png",
        "graphics/portraits/portrait_hegemony02.png", "graphics/portraits/portrait_luddic16.png",
        "graphics/portraits/portrait_luddic15.png", "graphics/portraits/portrait_luddic14.png",
        "graphics/portraits/portrait_luddic13.png", "graphics/portraits/portrait_luddic12.png",
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
        "communeLeader", "baseCommander", "administrator",
        // ranks
        "pilot", "elder", "clone",
        // ship names
        "Starhawk", "Solstice", "Sol Long", "Skyspark", "Redshift", "Ion Rose", "Cosmosis", "Triton", "Thisbe",
        "Themis", "Psyche", "Europa", "Chiron", "Taikonaut", "Starflyer", "Starfarer", "Space Dog", "Red Dwarf",
        "Mercurial", "Kosmoskva", "Kosmonaut", "High Moon", "Bon Comet", "Blueshift", "Blue Star", "Black Sun",
        "Venusian", "Stellarian", "Starfinder", "Stardriven", "Star Witch", "Spacefarer", "Solar Wind", "Nightfarer",
        "Green Moon", "Fair Orbit", "Pyramus", "Eunomia", "Via Acubens", "Stars Below", "Rigel Flyer", "Nightrunner",
        "Lightspeed+", "Lalande Ho!", "Gegenschein", "Caspian Sky", "Bright Burn", "Urania", "Stellar Wind",
        "Stellar Rose", "Space Raptor", "Kardashevian", "Dark Matters", "Comet's Tail", "Caloris Born",
        "Imbrium Flyer", "Fleeting Moon", "Ganymede", "Callisto", "Zodiacal Light", "Tachyon And On",
        "Sirius Express", "Ever Starbound", "Chatelain Thea", "Antares Bright", "Keter", "Ishim", "Orphan of Earth",
        "Memory of Deneb", "Gagarin's Dream", "Forgive Us Enos", "Cygnus Gleaming", "Aloft Parabolic",
        "7000 Lightyears", "Upon Starry Road", "Queen of Jupiter", "Plentiful Volume", "Mu Herculis Lost",
        "Korolev Dreaming", "Einstein's Demon", "Celestial Breeze", "Aldebaran Rising", "10000 Lightyears",
        "Pride of Achernar", "Polaris Traveller", "Old Earth Or Bust", "Interstellaration", "Interstellar Rain",
        "Third Moon Of Mars", "Laniakea Traveller", "Glory of the Stars", "Don't Stop Until C", "Arising Hyperbolic",
        "Prize of High Orbit", "Prince of the Stars", "Interstitial Breeze", "Hyperspace Dreaming",
        "Tsiolkovsky's Vision", "Apologies To Goddard",
        // stock variant ids
        "eradicator_pirates_Attack", "shade_Assault", "phantom_Elite", "mule_Standard", "drover_Strike",
        "valkyrie_Elite", "gremlin_Strike", "atlas_Standard", "medusa_PD", "ziggurat_Strike", "shrike_p_Attack",
        "hammerhead_d_CS", "glimmer_Assault", "falcon_p_Strike", "eagle_xiv_Elite", "atlas2_Standard",
        "apogee_Balanced", "anubis_Standard", "venture_p_Pirate", "sunder_d_Assault", "rampart_Standard",
        "prometheus_Super", "odyssey_Balanced", "legion_xiv_Elite", "hound_d_Standard", "harbinger_Strike",
        "colossus3_Pirate", "colossus2_Pather", "afflictor_Strike", "hound_d_pirates_Shielded", "wayfarer_Standard",
        "station2_Standard", "station1_Standard", "shard_left_Attack", "guardian_Standard", "enforcer_d_Strike",
        "centurion_Assault", "brilliant_Support", "berserker_Assault", "hound_d_pirates_Overdriven", "lasher_CS",
        "shard_left_Attack2", "enforcer_XIV_Elite", "brilliant_Standard", "bastillon_Standard",
        "scarab_Experimental", "onslaught_xiv_Elite", "dominator_XIV_Elite", "ox_Standard", "hermes_Standard",
        "gemini_Standard", "retribution_Standard", "prometheus2_Standard", "centurion_LG_Assault",
        "venture_pather_Attack", "skirmish_unit_Type101", "skirmish_unit_Type100", "shade_d_pirates_Assault",
        "mule_d_pirates_Standard", "mule_d_pirates_Smuggler", "colossus_Standard", "gremlin_d_pirates_Strike",
        "monitor_Escort", "enforcer_d_pirates_Strike", "enforcer_Assault", "gremlin_luddic_path_Strike",
        "afflictor_d_pirates_Strike", "manticore_luddic_path_Strike", "omen_PD", "shepherd_Frontier",
        "starliner_Standard", "tarsus_Standard", "buffalo_tritachyon_Standard", "buffalo_luddic_church_Standard",
        "phaeton_Standard", "wolf_hegemony_Assault", "hound_d_pirates_Standard", "buffalo_pirates_Standard",
        "buffalo_hegemony_Standard", "lasher_Standard", "brawler_Assault", "kite_Standard",
        "cerberus_d_pirates_Shielded", "wolf_Assault", "nebula_Standard",
        // variant display names
        "Light", "Experimental", "Custom", "Pirate", "Outdated", "Patrol", "Orbital Station", "Type 450", "Type 350",
        "Type 250", "Defense",
        // variant id suffixes
        "_Frontier", "_Light", "_Experimental", "_Pirate", "_Type450", "_Type350", "_Type250", "_Roiling", "_Attack2"
    )

    // Tier 3 (5465 bytes): expected saving per byte 0.75 .. 1.39
    private val tier3: List<String> = listOf(
        // fleet type names
        "Task Force", "Prospector", "Detachment", "Fast Picket", "Supply Fleet", "Liner Convoy", "Grand Armada",
        "Salvage Fleet", "Relief Convoy", "Investigators", "Bounty Hunter", "Mercenary Scout", "Luddic Pilgrims",
        "Mercenary Patrol", "Inspection Fleet", "Commerce Raiders", "Sacred Protectors", "Mercenary Company",
        "Mercantile Convoy", "Academy Shuttle Service",
        // fleet type ids
        "raider", "taskForce", "mercScout", "tradeLiner", "mercPatrol", "mercArmada", "supplyFleet", "smallTrader",
        "patrolSmall", "patrolLarge", "personBounty", "patrolMedium", "leagueArmada", "academyFleet",
        "tradeSmuggler", "mercPrivateer", "investigators", "battlestation", "shrinePilgrims", "scavengerSmall",
        "scavengerLarge", "leagueEnforcer",
        // hull ids
        "xyphos", "mule_d", "merlon", "hoplon", "dagger", "facet", "warthog", "trident", "thunder", "sarissa",
        "ravelin", "piranha", "longbow", "gladius", "eagle_d", "dem_drone", "eagle", "shuttlepod", "atlas2",
        "anubis", "gemini", "tarsus_d", "hermes_d", "gargoyle", "falcon_d", "lumen", "hound", "tarsus", "drone_pd",
        "nebula", "perdition", "mercury_d", "buffalo_d", "shrike", "scarab", "apogee", "hermes", "rampart",
        "phantom", "odyssey", "hound_d", "gremlin", "wolf", "heron", "cerberus_d", "broadsword", "revenant",
        "wolf_d", "warden", "sentry", "picket", "talon_tritachyon", "manticore_pather", "phaeton", "dominator_d",
        "falcon", "drover", "condor", "astral", "buffalo", "valkyrie", "sunder_d", "lasher_d", "falcon_p",
        "drone_borer", "mining_drone", "pegasus", "grendel", "fulgent", "colossus", "drone_sensor", "monitor",
        "sunder", "legion", "drone_assault", "venture_p", "harbinger", "eagle_xiv", "colossus3", "colossus2",
        "berserker", "bastillon", "shrouded_vortex", "shrouded_ejecta",
        // hullmod ids
        "bdeck", "rugged", "coherer", "vastbulk", "delicate", "civgrade",
        // memory keys
        "\$salvageDefFaction", "\$lightColorOverride", "\$lightSourceOverride", "\$cfai_ignoredByFaction",
        "\$cfai_makeNonAggressive", "\$sensorIndicatorsOverride", "\$everyoneJoinsBattleAgainst",
        "\$cfai_makeHostileToAllTradeFleets", "\$cfai_makeHostileToPlayerTradeFleets",
        "\$canOnlyBeEngagedWhenVisibleToPlayer", "\$salvageDOv", "\$atrocities", "\$suspectedAI", "\$fcm_eventRef",
        "\$stationMarket", "\$mayGoIntoAbyss", "\$recentlySalvaged", "\$overrideNoAutofit", "\$shrinePilgrimFleet",
        "\$resourceSalvageMult", "\$numGRInvestigations", "\$cfai_holdVsStronger", "\$recentlyPerformedRaid",
        "\$cfai_doNotIgnorePlayer", "\$noShipDerelictsPostBattle", "\$cfai_makePreventDisengage",
        "\$noHighBurnTopographyReadings", "\$core_fleetChasingGhostRandom", "\$cfai_allowPlayerBattleJoinTOff",
        "\$exceptionalSleeperPodOfficerOnSectorGen", "\$stationFleet",
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
        "graphics/portraits/portrait12.png", "graphics/portraits/portrait_mercenary04.png",
        "graphics/portraits/portrait42.png", "graphics/portraits/portrait34.png",
        "graphics/portraits/portrait33.png", "graphics/portraits/portrait21.png",
        "graphics/portraits/portrait22.png", "graphics/portraits/portrait_mercenary02.png",
        // posts
        "underworldBoss", "synodSubcurate", "excubitorOrbis", "medicalSupplier", "genericMilitary",
        "stationCommander", "personnelOfficer", "outpostCommander", "officer_for_hire", "marineSquadLeader",
        "commodities_agent", "nanoforge_engineer", "intelligenceDirector", "militaryAdministrator",
        "associate_academician", "assistant_academician",
        // ranks
        "sister", "mother", "father", "brother", "agent", "fugitive", "spaceChief", "spaceSailor", "houseLeader",
        "groundMajor", "knightCaptain", "groundPrivate", "groundGeneral", "groundColonel", "groundCaptain",
        "groundSergeant", "freedomFighter", "unknown", "citizen", "houseLeaderMale",
        // ship names
        "Never Again Chi Pegasi", "Queen of the Main Sequence", "Bright Spark", "Hafaza", "Erelim", "Elohim",
        "Azrael", "Azazel", "Adriel", "Raphael", "Ophanim", "Star Ark", "Seraphim", "Cherubim", "Hashmallim",
        "Bene Elohim",
        // skill ids
        "sensors", "salvaging", "omega_ecm", "navigation", "neural_link",
        // stock variant ids
        "sentry_FS", "buffalo_Standard", "dram_Light", "kite_luddic_path_Strike", "kite_luddic_path_Raider",
        "defender_PD", "buffalo2_FS", "wolf_d_Attack", "warden_Defense", "tempest_Attack", "picket_Assault",
        "lumen_Standard", "hound_Standard", "mercury_Standard", "brawler_LG_Elite", "hive_unit_Type350",
        "cerberus_d_pirates_Standard", "shard_right_Attack", "crig_Standard", "wolf_d_pirates_Attack",
        "shrouded_maw_Ravenous", "shrouded_eye_Darkened", "overseer_unit_Type250", "brawler_pather_Raider",
        "mudskipper_Standard", "fabricator_unit_Type450", "shrouded_tendril_Roiling", "lasher_luddic_path_Raider",
        "shrouded_maelstrom_Menacing", "brawler_tritachyon_Standard", "lasher_luddic_church_Standard",
        // variant display names
        "CGR Standard", "Shielded", "Escort", "Smuggler", "Point Defense",
        // variant id suffixes
        "_Ravenous", "_Menacing", "_Darkened", "_Starting", "_Defense", "_Outdated", "_Shielded", "_Escort",
        // weapon ids
        "novalaser", "lidardish", "sensordish", "minelayer2", "minelayer1", "clusterbomb", "blinker_red",
        "motelauncher", "lights_hound", "hydra_payload", "gazer_payload", "canister_flak", "blinker_green",
        "fragbomb", "pusherplate_lt", "lights_buffalo", "gorgon_payload", "flarelauncher3", "flarelauncher2",
        "flarelauncher1", "dragon_payload", "targetinglaser3", "targetinglaser2", "targetinglaser1",
        "pdburst_fighter", "od_bomblauncher", "nb_bomblauncher"
    )

    // Tier 4 (5456 bytes): expected saving per byte 1.39 .. 2.81
    private val tier4: List<String> = listOf(
        // faction ids
        "diktat_achilles", "derelict", "scavengers", "neutral",
        // fleet type ids
        "scavengerMedium", "inspectionFleet", "foodReliefFleet", "commerceRaiders", "sacredProtectors",
        "mercBountyHunter",
        // hull ids
        "glimmer", "gryphon", "ziggurat", "wayfarer", "invictus", "executor", "starliner", "tesseract", "venture",
        "prometheus", "enforcer_d", "drone_terminator", "drone_pd_midline", "shepherd", "tempest", "hyperion",
        "defender", "shard_right", "retribution", "prometheus2", "sunder_LG", "scintilla", "falcon_LG", "centurion",
        "afflictor", "paragon", "eagle_LG", "conquest", "kite_original", "hammerhead_d", "enforcer_xiv",
        "centurion_LG", "legion_xiv", "falcon_xiv", "hive_unit", "onslaught_xiv", "dominator_xiv",
        "module_hightech_attack_minelayer", "medusa", "attack_swarm", "buffalo2", "cerberus", "venture_pather",
        "shrike_pirates", "radiant", "onslaught", "shade_d_pirates", "vanguard", "brawler_LG", "mudskipper",
        "mercury", "brawler", "assault_unit", "buffalo_pirates", "gremlin_d_pirates", "brilliant", "shard_left",
        "aurora", "enforcer_d_pirates", "buffalo_hegemony", "hammerhead_LG", "gremlin_luddic_path",
        "afflictor_d_pirates", "buffalo_tritachyon", "manticore", "shrouded_maw", "shrouded_eye",
        "buffalo_luddic_church", "overseer_unit", "lasher_pather", "vanguard_pirates", "manticore_pirates",
        "wolf_d_pirates", "brawler_pather", "champion", "dominator", "standoff_unit", "eradicator_pirates",
        "fabricator_unit", "mudskipper2", "shrouded_tendril", "skirmish_unit", "shrouded_maelstrom",
        "brawler_tritachyon", "kite_pirates", "mule_d_pirates", "lasher_luddic_church", "lasher", "hammerhead",
        "hound_hegemony", "wolf_hegemony", "kite_hegemony", "hound_luddic_path", "eradicator", "hound_luddic_church",
        "cerberus_luddic_path", "vigilance", "kite_luddic_path",
        // hullmod ids
        "tow_cable", "fluxshunt", "comp_hull", "phasefield", "fourteenth", "comp_armor", "vast_hangar",
        "ill_advised", "faulty_auto", "damaged_deck", "comp_storage", "andrada_mods", "advancedcore",
        "supercomputer", "shard_spawner", "repair_gantry", "converted_bay", "axialrotation", "unstable_coils",
        "threat_hullmod", "shielded_holds", "never_detaches", "missile_reload", "manual_systems", "ground_support",
        "ex_phase_coils", "damaged_mounts", "anchorrotation", "ablative_armor", "low_maintenance", "heg_militarized",
        "dweller_hullmod", "do_not_back_off", "chassis_storage", "always_detaches", "shield_always_on",
        "shared_flux_sink", "high_maintenance", "glitched_sensors", "erratic_injector", "destroyed_mounts",
        "degraded_shields", "stealth_minefield", "reduced_explosion", "fragile_subsystems", "design_compromises",
        "do_not_fire_through", "stealth_minefield_lt", "malfunctioning_comms", "degraded_drive_field",
        "increased_maintenance", "degraded_life_support", "defective_manufactory", "drive_field_stabilizer",
        "advanced_ground_support", "distributed_fire_control", "automated", "terminator_core",
        // memory keys
        "\$officerMaxLevel", "\$claimingFaction", "\$forceTOff", "\$stationBaseFleet", "\$smugglingSuspicion",
        "\$salvageDebrisField", "\$justDidSensorBurst", "\$fleetBuysStuffRate", "\$sensorBurstJustUsed",
        "\$officerMaxEliteSkills", "\$extraSensorIndicators", "\$doNotInitCommListings", "\$fcm_faction",
        "\$officerSkillPicksPerLevel", "\$hasCustomInteractionOptions", "\$numTimesSatBombardedByPlayer",
        "\$cfai_recentlyDefeatedByPlayer", "\$fleetType", "\$willHasslePlayer", "\$recentlyBombarded",
        "\$keepPursuingPlayer", "\$cfai_makeFleePlayer", "\$isRaider", "\$patrolExtraSuspicion",
        "\$marketExtraSuspicion", "\$shownFleetDescAlready", "\$justToggledTransponder", "\$suspendMilitaryResponse",
        "\$justDidInterdictionPulse", "\$patrol", "\$interdictionPulseJustUsed", "\$alwaysSpreadTOffHostility",
        "\$core_fleetMilitaryResponse", "\$exceptionalSleeperPodOfficer", "\$hassleType", "\$isCustomsInspector",
        "\$sawPlayerTransponderOn", "\$fleetBuysStuff", "\$isWarFleet", "\$core_hiddenBase",
        "\$cfai_makeAggressiveLastsOneBattle", "\$cfai_avoidPlayerSlowly",
        // person memory keys
        "\$rank", "\$post", "\$name", "\$level",
        // portraits
        "graphics/portraits/portrait_mercenary01.png", "graphics/portraits/portrait_mercenary08.png",
        "graphics/portraits/portrait_ai3b.png", "graphics/portraits/portrait_ai2b.png",
        "graphics/portraits/portrait37.png", "graphics/portraits/portrait28.png",
        "graphics/portraits/portrait29.png", "graphics/portraits/portrait20.png",
        "graphics/portraits/portrait15.png", "graphics/portraits/portrait_mercenary03.png",
        "graphics/portraits/portrait18.png", "graphics/portraits/portrait16.png",
        // ranks
        "groundLieutenant", "houseLeaderFemale", "deputyStarMarshal", "chiefHighInspector", "criminal",
        "artificialIntelligence", "guardHighDeputyExecutor", "terrorist", "executive", "aristocrat", "specialAgent",
        "factionLeader", "seniorExecutive", "spaceAdmiral",
        // ship names
        "Hayot Ha Kodesh", "Unnamed", "Nameless",
        // ship name prefixes
        "RSDS ", "DSS ", "01011001 ",
        // skill ids
        "weapon_drills", "field_repairs", "bulk_transport", "automated_ships", "aptitude_combat", "space_operations",
        "hull_restoration", "colony_management", "auxiliary_support", "aptitude_industry", "makeshift_equipment",
        "aptitude_technology", "aptitude_leadership", "planetary_operations", "special_modifications",
        "containment_procedures",
        // stock variant ids
        "kite_pirates_Raider", "hound_hegemony_Standard", "hound_luddic_path_Attack", "kite_hegemony_Interceptor",
        "cerberus_luddic_path_Attack", "hound_luddic_church_Standard",
        // variant display names
        "Overdriven", "Battlestation", "Interceptor", "Fire Support", "Balanced", "Raider",
        // variant id suffixes
        "_Overdriven", "_Interceptor", "_Raider", "_Balanced",
        // voices
        "pather", "aristo",
        // weapon ids
        "motelauncher_hf", "interdictorbeam", "flarelauncher21", "pusherplate_nova", "terminator_missile",
        "riftbeam_minelayer", "riftlance_minelayer", "lightmortar_fighter", "pusherplate_lt_small",
        "riftcascade_minelayer", "rift_lightning_minelayer", "sabot_fighter", "phasecl_bomber", "bomb",
        "swarmer_fighter", "irpulse_fighter", "ioncannon_fighter", "heavy_adjudicator", "annihilator_fighter",
        // fighter wing ids
        "borer_wing", "sarissa_wing", "terminator_wing", "mining_drone_wing", "attack_swarm_wing",
        "aspect_shock_wing", "aspect_attack_wing", "aspect_missile_wing", "shrouded_vortex_wing",
        "shrouded_ejecta_wing", "reclamation_swarm_wing", "construction_swarm_wing", "aspect_shieldbreaker_wing",
        "wasp_wing", "cobra_wing", "lux_wing", "xyphos_wing"
    )

    // Tier 5 (5459 bytes): expected saving per byte 2.84 .. 119.28
    private val tier5: List<String> = listOf(
        // ai cores
        "alpha_core", "beta_core", "gamma_core",
        // faction ids
        "mercenary", "remnants", "lions_guard", "knights_of_ludd", "luddic_path", "luddic_church", "hegemony",
        "persean_league", "sindrian_diktat", "tritachyon", "independent", "pirates", "player",
        // hull ids
        "enforcer", "hound_d_pirates", "cerberus_d_pirates", "guardian",
        // hullmod ids
        "faulty_grid", "comp_structure", "degraded_engines", "high_frequency_attractor", "no_weapon_flux", "eccm",
        "nav_relay", "hiressensors", "pdintegration", "phase_anchor", "frontemitter", "escort_package",
        "assault_package", "adaptiveshields", "shrouded_lens", "neural_interface", "high_scatter_amp", "autorepair",
        "frontshield", "adaptive_coils", "neural_integrator", "shrouded_mantle", "shield_shunt",
        "surveying_equipment", "additional_berthing", "shrouded_thunderhead", "expanded_cargo_holds",
        "converted_fighterbay", "recovery_shuttles", "solar_shielding", "augmentedengines", "auxiliary_fuel_tanks",
        "defensive_targeting_array", "operations_center", "efficiency_overhaul", "secondary_fabricator",
        "fragment_coordinator", "advancedshieldemitter", "pointdefenseai", "extendedshieldemitter",
        "expanded_deck_crew", "militarized_subsystems", "fragment_swarm", "fluxcoil", "turretgyros",
        "converted_hangar", "reinforcedhull", "missile_autoloader", "safetyoverrides", "ballistic_rangefinder",
        "stabilizedshieldemitter", "blast_doors", "unstable_injector", "hardened_subsystems", "advancedoptics",
        "insulatedengine", "magazines", "heavyarmor", "armoredweapons", "auxiliarythrusters", "fluxbreakers",
        "dedicated_targeting_core", "fluxdistributor", "hardenedshieldemitter", "missleracks", "targetingunit",
        // memory keys
        "\$requiresDiscretionToDeal", "\$core_fleetNoMilitaryResponse", "\$cfai_makeAlwaysPursue",
        "\$cfai_makeAllowDisengage", "\$ignorePlayerCommRequests", "\$core_fleetNotChasingGhost", "\$noShipRecovery",
        "\$military", "\$core_fleetChasingGhost", "\$core_fleetSpecialAction", "\$fidConifgGen",
        "\$cfai_ignoredByOtherFleets", "\$fidConifg", "\$cfai_noJump", "\$playerHostileTimeout", "\$patrolAllowTOff",
        "\$recentlyRaided", "\$doNotGetSidetracked", "\$isSmuggler", "\$pursuePlayer", "\$sawPlayerTransponderOff",
        "\$noRepImpact", "\$cfai_makeNonHostile", "\$cfai_longPursuit", "\$isTradeFleet", "\$objectiveNonFunctional",
        "\$isPatrol", "\$salvageSpecialData", "\$cfai_makeHostileWhileTOff", "\$lowRepImpact", "\$isPirate",
        "\$missionImportant", "\$salvageSpecId", "\$cfai_ignoreOtherFleets", "\$salvageSeed", "\$sourceMarket",
        "\$cfai_makeAggressive", "\$core_fleetBusy", "\$cfai_makeHostile",
        // person memory keys
        "\$rankId", "\$postId", "\$isPerson", "\$isContact", "\$personName", "\$importance", "\$personality",
        // personalities
        "timid", "reckless", "cautious", "aggressive", "steady",
        // portraits
        "graphics/portraits/portrait_ai1b.png",
        // posts
        "patrolCommander", "fleetCommander", "officer",
        // ranks
        "spaceEnsign", "spaceCaptain", "spaceCommander", "spaceLieutenant",
        // ship name prefixes
        "LSS ", "LGS ", "SDS ", "PLS ", "CGR ", "TTDS ", "TTS ", "HSS ", "ISS ",
        // skill ids
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
        "Elite", "Close Support", "Support", "Strike", "Assault", "Attack", "Standard",
        // variant id suffixes
        "_Elite", "_Support", "_Strike", "_Attack", "_Assault", "_Standard",
        // variant tags
        "fx_drone", "unboardable", "unrestorable", "no_ai_core_drop", "always_recoverable",
        "consistent_weapon_drops", "variant_allow_excess_op_etc", "variant_always_retain_smods_on_salvage",
        // voices
        "spacer", "villain", "soldier", "official", "faithful", "business", "scientist",
        // weapon ids
        "hydra", "dragon", "gauss", "gorgon", "gazer", "locust", "vpdriver", "gorgonpod", "phasecl", "cyclone",
        "sabot_single", "rifttorpedo", "gazerpod", "cryoflux", "amsrm", "riftcascade", "pilum_large", "plasma",
        "hephag", "hammer_single", "jackhammer", "heavyburst", "atropos", "realitydisruptor", "hammerrack",
        "gigacannon", "swarm_launcher", "squall", "riftbeam", "mjolnir", "voidblaster", "hellbore", "dragonpod",
        "rift_lightning", "salamanderpod", "abyssal_glare", "vortex_launcher", "devouring_swarm", "multineedler",
        "pilum", "riftlance", "mark9", "mininglaser", "cryoblaster", "hurricane", "pseudoparticle_jet", "heavymg",
        "voltaic_cannon", "harpoon_single", "atropos_single", "neutron_torpedo", "breach", "assaying_rift",
        "neoferric_quadcoil", "shredder", "seeker_fragment", "breachpod", "amblaster", "chaingun", "miningblaster",
        "heavyblaster", "railgun", "ionbeam", "unstable_fragment", "irautolance", "phasebeam", "disintegrator",
        "autopulse", "swarmer", "flak", "dualflak", "harpoonpod", "resonatormrm", "heavy_mass_driver",
        "kineticblaster", "kinetic_fragments", "ionpulser", "heavymortar", "heavymauler", "devastator", "hammer",
        "typhoon", "tachyonlance", "minipulser", "lightmortar", "sabot", "voltaic_discharge", "lightag",
        "annihilatorpod", "irpulse", "lrpdlaser", "heavyneedler", "lightdualac", "reaper", "inimical_emanation",
        "heatseeker", "light_mass_driver", "annihilator", "sabotpod", "arbalest", "lightneedler", "ioncannon",
        "taclaser", "hveldriver", "lightac", "heavyac", "pulselaser", "lightdualmg", "shockrepeater", "harpoon",
        "lightmg", "pdburst", "gravitonbeam", "vulcan", "pdlaser",
        // fighter wing ids
        "claw_wing", "spark_wing", "trident_wing", "warthog_wing", "longbow_wing", "gladius_wing", "dagger_wing",
        "talon_wing", "piranha_wing", "perdition_wing", "flash_wing", "thunder_wing", "hoplon_wing",
        "broadsword_wing"
    )

    val v2: ByteArray = build(tier0 + tier1 + tier2 + tier3 + tier4 + tier5)
}