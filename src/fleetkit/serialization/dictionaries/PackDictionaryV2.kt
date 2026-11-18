package fleetkit.serialization.dictionaries

import fleetkit.serialization.core.ByteBuffers

/**
 * Preset zlib dictionary for serialized VariantAPI / FleetMemberAPI / PersonAPI / CampaignFleetAPI data.
 *
 * NEVER edit this once codes have shipped. Make a new version instead.
 *
 * Layout, from the START of the dictionary (farthest from the data, cheapest to lose) to the END:
 *   pairs   : runs of consecutive entries from one map/list (slot id -> weapon id, adjacent hullmod / wing / tag ids).
 *             Listed in stream order, so one deflate match can cover several strings at once.
 *   tier0-5 : single strings, least valuable -> most valuable. Entries within a tier are grouped by kind.
 * zlib only uses the last 32 KB, so the total must stay <= 32768 bytes (checked at startup).
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

    // Pairs (6496 bytes): two consecutive map/list entries each (slot -> weapon x2, adjacent hullmods / wings / tags)
    private val pairs: List<String> = listOf(
        "glitched_sensors", "fragile_subsystems",
        "hardenedshieldemitter", "targetingunit", "advancedoptics", "hardenedshieldemitter", "WS 002", "hveldriver",
        "WS 004", "hveldriver", "WS 004", "harpoonpod", "WS 005", "harpoonpod",
        "targetingunit", "stabilizedshieldemitter", "auxiliarythrusters", "missile_autoloader",
        "hardened_subsystems", "auxiliarythrusters", "WS 003", "annihilator", "WS 004", "annihilator",
        "WS 005", "lightdualac", "WS 006", "lightdualac", "WS 001", "lightneedler", "WS 002", "lightneedler",
        "WS 003", "lightneedler", "WS 004", "lightneedler", "WS 004", "lightneedler", "WS 005", "lightneedler",
        "WS 006", "gravitonbeam", "WS 007", "gravitonbeam", "WS 006", "lightneedler", "WS 007", "lightneedler",
        "extendedshieldemitter", "hardenedshieldemitter", "cobra_wing", "cobra_wing", "talon_wing", "talon_wing",
        "heavyarmor", "missleracks", "cobra_wing", "trident_wing", "missleracks", "fluxbreakers", "WS 001", "flak",
        "WS 005", "flak", "heavyarmor", "insulatedengine", "targetingunit", "fluxbreakers",
        "armoredweapons", "missleracks", "missleracks", "fluxdistributor", "turretgyros", "fluxdistributor",
        "armoredweapons", "targetingunit", "WS 007", "breach", "WS 008", "breach", "WS 008", "breach",
        "WS 009", "breach", "armoredweapons", "insulatedengine", "WS 001", "pdlaser", "WS 002", "pdlaser",
        "WS 002", "atropos", "WS 003", "atropos", "WS 003", "pdburst", "WS 004", "pdburst", "WS 004", "ionbeam",
        "WS 005", "ionbeam", "WS 004", "pdburst", "WS 005", "pdburst", "WS 007", "pdlaser", "WS 008", "pdlaser",
        "WS 008", "pdlaser", "WS 011", "pdlaser", "WS 009", "pdburst", "WS 010", "pdburst", "WS 010", "pdlaser",
        "WS 011", "pdlaser", "WS 016", "pdlaser", "WS 017", "pdlaser", "damaged_mounts", "erratic_injector",
        "broadsword_wing", "broadsword_wing", "targetingunit", "missile_autoloader", "WS 000", "irpulse",
        "WS 001", "ioncannon", "WS 001", "ioncannon", "WS 002", "irpulse", "no_autofit", "consistent_weapon_drops",
        "WS 002", "lrpdlaser", "WS 003", "lrpdlaser", "WS 002", "phasebeam", "WS 003", "phasebeam",
        "WS 004", "breachpod", "WS 005", "breachpod", "WS 005", "phasebeam", "WS 006", "phasebeam",
        "WS 009", "ioncannon", "WS 010", "ioncannon", "WS 003", "gravitonbeam", "WS 004", "ionbeam",
        "WS 005", "ionbeam", "WS 006", "gravitonbeam", "WS 000", "gravitonbeam", "WS 005", "gravitonbeam",
        "WS 000", "tachyonlance", "WS 002", "tachyonlance", "WS 002", "lightneedler", "WS 003", "lightneedler",
        "WS 007", "heavyneedler", "WS 008", "heavyneedler", "fluxbreakers", "targetingunit", "WS 005", "sabot",
        "WS 006", "sabot", "WS 001", "reaper", "WS 004", "reaper", "WS 002", "railgun", "WS 004", "railgun",
        "WS 005", "lightac", "WS 006", "lightac", "WS 007", "harpoon", "WS 008", "harpoon", "WS 007", "heavyac",
        "WS 008", "heavyac", "insulatedengine", "unstable_injector", "WS 001", "lrpdlaser", "WS 002", "lrpdlaser",
        "WS 001", "lightdualmg", "WS 003", "dualflak", "WS 003", "dualflak", "WS 005", "lightdualmg",
        "WS 004", "railgun", "WS 006", "lightneedler", "WS 002", "gravitonbeam", "WS 003", "gravitonbeam",
        "turretgyros", "missleracks", "trident_wing", "trident_wing", "armoredweapons", "heavyarmor",
        "turretgyros", "armoredweapons", "blast_doors", "fluxdistributor", "targetingunit", "reinforcedhull",
        "targetingunit", "fluxdistributor", "WS 001", "lightac", "WS 002", "lightag", "WS 001", "pdlaser",
        "WS 004", "pdlaser", "WS 002", "lightag", "WS 004", "lightac", "WS 005", "harpoon", "WS 006", "harpoon",
        "WS 006", "pdlaser", "WS 007", "pdlaser", "WS 009", "pdlaser", "WS 010", "pdlaser", "WS 001", "lrpdlaser",
        "WS 002", "vulcan", "WS 004", "lightac", "WS 005", "ioncannon", "fluxdistributor", "hardenedshieldemitter",
        "WS 005", "lightdualmg", "WS 007", "lightdualmg", "WS 005", "gravitonbeam", "WS 006", "gravitonbeam",
        "hardenedshieldemitter", "hardened_subsystems", "fluxdistributor", "fluxcoil", "WS 002", "vulcan",
        "WS 003", "vulcan", "WS 001", "harpoon", "WS 002", "harpoon", "WS 011", "pdburst", "WS 012", "pdburst",
        "lux_wing", "flash_wing", "WS 001", "lightdualmg", "WS 002", "lightdualmg", "WS 005", "lightneedler",
        "WS 006", "lightneedler", "flash_wing", "flash_wing", "missleracks", "eccm", "missleracks", "targetingunit",
        "WS 000", "pilum", "WS 001", "pilum", "WS 001", "pilum", "WS 002", "pilum", "WS 002", "pilum",
        "WS 003", "pilum", "broadsword_wing", "thunder_wing", "WS 000", "hephag", "WS 002", "hephag",
        "WS 006", "hephag", "WS 008", "hephag", "WS 017", "vulcan", "WS 018", "vulcan", "WS 021", "vulcan",
        "WS 022", "vulcan", "WS 002", "hephag", "WS 003", "mjolnir", "WS 005", "mjolnir", "WS 006", "hephag",
        "WS 001", "swarmer", "WS 002", "swarmer", "WS 003", "mjolnir", "WS 005", "mjolnir", "WS 003", "pdlaser",
        "WS 004", "pdlaser", "WS 004", "lightmg", "WS 005", "lightmg", "WS 010", "pdburst", "WS 011", "pdburst",
        "WS 001", "guardian", "WS 002", "guardian", "WS 002", "pulselaser", "WS 004", "pdlaser",
        "WS 004", "heatseeker", "WS 009", "heatseeker", "WS 001", "multineedler", "WS 004", "multineedler",
        "WS 004", "multineedler", "WS 007", "multineedler", "WS 010", "gravitonbeam", "WS 011", "gravitonbeam",
        "WS 011", "gravitonbeam", "WS 012", "gravitonbeam", "WS 012", "gravitonbeam", "WS 013", "gravitonbeam",
        "WS 018", "vulcan", "WS 019", "vulcan", "WS 019", "vulcan", "WS 020", "vulcan", "WS 003", "lightdualmg",
        "WS 004", "lightdualmg", "WS 013", "gravitonbeam", "WS 014", "gravitonbeam", "WS 014", "gravitonbeam",
        "WS 015", "gravitonbeam", "WS 001", "gauss", "WS 014", "gauss", "hoplon_wing", "broadsword_wing",
        "lux_wing", "spark_wing", "WS 000", "vulcan", "WS 003", "vulcan", "WS 016", "vulcan", "WS 017", "vulcan",
        "WS 000", "dualflak", "WS 002", "vulcan", "WS 002", "vulcan", "WS 006", "dualflak", "WS 002", "pdlaser",
        "WS 003", "pdlaser", "WS 006", "dualflak", "WS 007", "vulcan", "WS 008", "vulcan", "WS 009", "dualflak",
        "WS 009", "dualflak", "WS 012", "vulcan", "WS 012", "vulcan", "WS 013", "dualflak", "WS 015", "mjolnir",
        "WS 016", "mjolnir", "WS 001", "dualflak", "WS 002", "dualflak", "spark_wing", "spark_wing",
        "WS 004", "heavymauler", "WS 010", "heavymauler", "hoplon_wing", "hoplon_wing", "WS 005", "lightmg",
        "WS 006", "lightmg", "WS 006", "pdburst", "WS 007", "pdburst", "WS 003", "pulselaser", "WS 004", "ionpulser",
        "WS 002", "lightdualmg", "WS 003", "lightdualmg", "WS 003", "lightmg", "WS 004", "lightmg",
        "WS 001", "pulselaser", "WS 002", "pulselaser", "WS 004", "pdlaser", "WS 005", "pdlaser",
        "variant_always_retain_smods_on_salvage", "no_autofit", "WS 004", "vulcan", "WS 005", "vulcan",
        "WS 001", "heatseeker", "WS 002", "heatseeker", "WS 003", "vulcan", "WS 004", "vulcan", "WS 005", "vulcan",
        "WS 006", "vulcan", "WS 002", "lightmg", "WS 003", "lightmg", "WS 001", "vulcan", "WS 002", "vulcan",
        "WS 006", "vulcan", "WS 007", "vulcan", "WS 001", "lightmg", "WS 002", "lightmg", "WS 007", "vulcan",
        "WS 008", "vulcan", "WS 000", "mark9", "WS 003", "mark9", "WS 001", "pilum", "WS 003", "pilum",
        "broadsword_wing", "piranha_wing", "WS 007", "vulcan", "WS 010", "vulcan", "WS 010", "vulcan",
        "WS 012", "vulcan", "WS 013", "phasecl", "WS 015", "vulcan", "WS 002", "dualflak", "WS 005", "vulcan",
        "WS 005", "typhoon", "WS 008", "typhoon", "WS 010", "phasecl", "WS 013", "phasecl", "WS 009", "dualflak",
        "WS 010", "phasecl", "WS 001", "hellbore", "WS 002", "hellbore", "WS 004", "dualflak", "WS 006", "dualflak",
        "WS 006", "dualflak", "WS 007", "dualflak", "WS 007", "dualflak", "WS 009", "dualflak", "WS 011", "sabotpod",
        "WS 012", "sabotpod", "WS 000", "hveldriver", "WS 004", "hveldriver", "WS 015", "vulcan", "WS 018", "vulcan",
        "WS 018", "vulcan", "WS 020", "vulcan", "WS 021", "vulcan", "WS 023", "vulcan", "WS 023", "vulcan",
        "WS 026", "vulcan", "WS 008", "salamanderpod", "WS 009", "salamanderpod", "WS 005", "vulcan",
        "WS 007", "vulcan", "WS 020", "vulcan", "WS 021", "vulcan"
    )

    // Tier 0 (4388 bytes): expected saving per byte 0.00 .. 0.00
    private val tier0: List<String> = listOf(
        // person names
        "Master-blaster", "Slaughterhouse", "Rite-of-earth", "Faith-my-joy", "Christensen", "Scrapstrike",
        "Charbonnier", "Spacewalker", "Sierra Azul", "Loma Prieta", "Kentaurian", "California", "Mont-Royal",
        "Jean-Talon", "Ono-Sendai", "Betelgeuse", "Harrington", "Livingston", "Desjardins", "Whittemore",
        "Mercaillon", "Prefabrier", "Anno Nuevo",
        // posts
        "guardLeader", "activist", "warlord", "agent",
        // ranks
        "spaceLieutenant", "spaceMarshal", "spaceEnsign", "spaceSailor", "spaceChief",
        // ship names
        "Breq Station", "Yama Station", "Nova Maxios", "Kanta's Den", "Tech Lab 13", "Ultraviolet", "Harpocrates",
        "Neoptolemus", "Free Evenus", "Serendipity", "Gilden Good", "Lalande Ho!", "Aladdin-San", "Lambda-Jake",
        "Aleph-Angel", "Ayin-Buster", "Thunder-Liu", "Free Plenty", "Keuthonymos", "Concordance", "Voidstalker",
        "Fated Death", "Terpsichore", "Blue Sphinx", "Kappa-Daisy", "Kane Gleise", "Kali Molina", "Old Soldier",
        "Nemestrinus", "Itzpapalotl", "Voidclipper", "Tigra City", "Amphitrite", "Antiphates", "Mog Ruitas",
        "Vikinguard", "Enterprise", "Stellarian", "Chalmecatl", "Cymopoleia", "Amphinomus", "Eurymachus",
        "Malfeasant", "By-your-by", "Xenorphica", "Aeternitas", "Novensilus", "White Pine", "Hashmallim",
        "Green Mars", "Green Moon", "Zeta-Molly", "Taw-Maggie", "Res-Sophie", "Ghost Bird", "Flagellant",
        "Death Star", "Lightyears", "Lightwitch", "Xipe Totec", "Vanquisher", "Prometheus", "Erechtheus",
        "Dawn Taken", "Inevitable", "Old Zodiac", "Vindicator", "Antimachus", "Hypericity", "Roman Road",
        "Miraculux", "Andraemon", "Cyhiraeth", "Electryon", "Scrivener", "Purgatory", "Demonassa", "Tyndareus",
        "Kosmonara", "Alcyoneus", "Aegialeia", "Amechania", "Pygmalion", "Cailleach", "Red Forge", "Nullifier",
        "Hadhoyosh", "Trooperon", "Hyperitas", "Admirable", "Pellinore", "Necessity", "Red Dwarf", "Starbound",
        "Tau-Queen", "Hector-Yi", "Autolycus", "Long Shot", "Gray Area", "Ambergris", "Vengeance", "Eurycleia",
        "Gandareva", "Narcissus", "Chalcodon", "Iron Tomb", "Bergelmir", "Atlatonin", "Sergestus", "Alectrona",
        "Blue Mars", "Mezentius", "Concordia", "Scrimshaw", "Fabulinus", "Venturous", "Trenchant", "Audacious",
        "Daedalion", "Aristaeus", "Appa-Duke", "Tisiphone", "Ferentina", "Endurance", "Dauntless", "Palinurus",
        "Undaunted", "Vulturnus", "Clementia", "Advantage", "Ixtlilton", "Venusian", "Antiphus", "Agamedes",
        "Diomedes", "Demophon", "Proioxis", "Astraeus", "Ceridwen", "Patecatl", "Borghild", "Epiphron", "Cerberus",
        "Hysminai", "Antinous", "Dechtere", "Cethlion", "Dioscuri", "Tartarus", "Obarator", "Basilisk", "Mayahuel",
        "Amon Roc", "Deioneus", "Epicasta", "Big Breg", "Aethlius", "Alizarin", "Atymnius", "Lone Pig", "Chamrosh",
        "Neheb-ka", "Castores", "Asterius", "Amenthes", "Kamidana", "Humility", "Amon-rex", "Valkyrie", "Mixcoatl",
        "Psi-Duke", "Nu-Rusty", "Mu-Buddy", "Pi-Rover", "Misirlou", "Ptocheia", "Tlachtga", "Corymbus", "Taliesin",
        "Voyageur", "Redshift", "Shabrang", "Hate-sin", "Hivescum", "Centeotl", "Minotaur", "Werewolf", "Cocidius",
        "Nephelai", "Mephitis", "Duellona", "Latarius", "Pentheus", "Augustus", "Talassio", "Carnabon", "Aequitas",
        "Nausicaa", "Aegyptus", "Atalanta", "Appiades", "Victoria", "Solstice", "Neptune", "Andreus", "Chryses",
        "Andarta", "Gwydion", "Alpheus", "Andvari", "Eleusis", "Acestes", "Oedipus", "Maponos", "Egestes", "Lampads",
        "Muspell", "Tamesis", "Simurgh", "Outcast", "Euryale", "Triteia", "Amymone", "Ogyruan", "Pandora", "Achates",
        "Cyzicus", "Serapis", "Deverra", "Amaunet", "Croesus", "Romulus", "Veritas", "Pandion", "Starbat", "Mujinto",
        "Butsudo", "Miracle", "Lustral", "Puma-Er", "Priamus", "Morholt", "Deipyle", "Curtius", "Hygieia", "Gehenna",
        "Raphael", "Caledon", "Nechtan", "Camilla", "Virbius", "Juturna", "Triumph", "Badmark", "Thaumas", "Zetamax",
        "Camenae", "Tracker", "Stilbon", "Buzzard", "Ichnaea", "Epiales", "Azimute", "Soranus", "Feronia", "Occator",
        "Ulysses", "Avernus", "Midgard", "Tyrfing", "Battle", "Ambush", "Indigo", "Hedone", "Aloeus", "Muteor",
        "Pelops", "Sophie", "Xuthus", "Memnon", "Nodens", "Hermod", "Xocotl", "Amycus", "Reward", "Aphaea", "Elatus",
        "Modron", "Nostos", "Wyvern", "Pariah", "Acamas", "Aergia", "Coelus", "Kelpie", "Eleius", "Empusa", "Myrrha",
        "Questa", "Epeius", "Ananke", "Makhai", "Tarvos", "Phrike", "Neleus", "Cleite", "Nereus", "Zahhak", "Agrius",
        "Creusa", "Pandia", "Semele", "Jarvee", "Gareth", "Orphne", "Benkei", "Hez-ur", "Nebtuu", "Sigurd", "Hafaza",
        "Tlaloc", "Muninn", "Gawain", "Tycoon", "Erebus", "Teucer", "Crisus", "Decima", "Nundia", "Astute", "Horkos",
        "Aimend", "Kraken", "Aibell", "Viking", "Vesper", "Borage", "Consus", "Daring", "Talent", "Procas", "Kronos",
        "Codrus", "Somnus", "Selene", "Stheno", "Aegeus", "Amber", "Cromo", "Asius", "Shoth", "Herve", "Horme",
        "Gwenn", "Nesoi", "Elpis", "Aesir", "Etain", "Aceso", "Olwen", "Iambe", "Ixion", "Aleus", "Gwynn", "Nanna",
        "Epona", "Saone", "Eleos", "Narah", "Fraus", "Ocnus", "Lelex", "Vesta", "Methe", "Baron", "Petbe", "Vanir",
        "Ament", "Jotus", "Diana", "Borus", "Terra", "Boxer", "Ishim", "Keter", "Arkon", "Herem", "Bragi", "Aegir",
        "Risky", "Aegle", "Corus", "Mneme", "Jason", "Mabon", "Buraq", "Dylan", "Bird", "Ullr", "Wyrm", "Plur",
        "Caer", "Eriu", "Aine", "Loki", "Elen", "Auge", "Ghul", "Echo", "Benu", "Shai", "Ymir", "Jord", "Piri",
        "Azhi", "Bres", "Garm", "Solo", "Argo", "Nyx", "Sul", "Roc", "Dev"
    )

    // Tier 1 (4371 bytes): expected saving per byte 0.00 .. 0.01
    private val tier1: List<String> = listOf(
        // person memory keys
        "exceptionalSleeperPodOfficer",
        // person names
        "Blackwell", "Contreras", "Serpentis", "Valentine", "Lancaster", "Luytenian", "Cytherean", "Chaparral",
        "Turbineer", "Calderone", "Wilkerson", "McFarland", "Spinnaker", "MacDonald", "Hochelaga", "Chanthira",
        "Aldebaran", "Nicholson", "Miramichi", "Middleton", "Donaldson", "Andronica", "Alderamin", "Teegarden",
        "Anastasia", "Frederick", "Stevenson", "Schroeder", "Patterson", "Daugherty", "Gabriella", "Peterson",
        "Beaubien", "Copeland", "Australe", "Chiasson", "Petersen", "Iskander", "Rosemary", "Torcular", "Jacobson",
        "Faulkner", "Lawrence", "Octagens", "Vittoria", "Benjamin", "Weep-not", "McFadden", "Stephens", "Alvarado",
        "Forillon", "Holloway", "Modulier", "Boudicca", "Cryoborn", "Williams", "Severine", "McDonald", "Titanian",
        "Fitzhugh", "Artifice", "Tau Ceti", "Zambrano", "Gonzales", "Valcourt", "Champoux", "Rosemont", "Melendez",
        "Galloway", "Kirkland", "Browning", "Saunders", "Johnston", "Arcturus", "Delacruz", "Peltonen", "Centauri",
        "Tiberius", "Jaunzeme", "Aphelion", "Bouchard", "McCarthy", "Moncton", "Halyard", "Charles", "Meadows",
        "Adebayo", "Kherlen", "Langley", "Procyon", "Floreal", "Ormston", "Swanson", "Spencer", "Giennah", "Walters",
        "Pleione", "Trinity", "Schaffa", "Rasalas", "Freeman", "Imbrium", "Mintaka", "Farrell", "Tarazet", "Horacio",
        "Michael", "Olawale", "Sideris", "Holland", "Beaudry", "Pickett", "Phlegra", "Bandler", "Ishtanu", "Shriver",
        "Balance", "Octavia", "Holcomb", "Rossian", "Hancock", "Schultz", "Ventose", "Mantaro", "Borozan", "Martian",
        "Olabisi", "Butcher", "Chronis", "Septrum", "Leblanc", "Sirenum", "Bennett", "Claudia", "Salazar", "Quesada",
        "Sanders", "Morcenx", "Leonard", "Elliott", "Acevedo", "English", "Cascade", "Halward", "Bullock", "Korshid",
        "Buckley", "Matawak", "Foreman", "Osborne", "Gregory", "Maragos", "Stanton", "Pittman", "McGuire", "Bernard",
        "Jackson", "Gallery", "Cochran", "Parrish", "Mindaro", "Hendrix", "Eridani", "Workman", "Clemons", "Aguilar",
        "Sherman", "Navarro", "Bentley", "Umbriel", "Goodman", "Jinxing", "Angelis", "Mosolov", "Patrick", "Robbins",
        "Conley", "Vasona", "Lauzon", "Naimar", "Juneau", "Polana", "Varuna", "Arnold", "Obrien", "Deader", "Naimal",
        "Snider", "Rankin", "Abaeze", "Dalmar", "Thomas", "Barron", "Romero", "Jacobs", "Negasi", "Shrial", "Boyana",
        "Kramer", "Clovis", "Bondar", "Apollo", "Daniel", "Hudson", "Hathor", "Howard", "Morris", "Landry", "Hermes",
        "Cioban", "Harris", "Persia", "Robles", "Wagner", "Hansen", "Herman", "Chayat", "Common", "Little", "Carson",
        "Nguyen", "Cutler", "Farmer", "Summer", "Kianna", "Jabbah", "Flitch", "Pruitt", "Lucida", "Valver", "Barker",
        "Mexico", "Willis", "Zoputa", "Tertia", "Ramsey", "Nevena", "Kochab", "Okafor", "Rogers", "Newton", "Drolet",
        "Oliver", "Golden", "Hadean", "Marois", "Curran", "Diziet", "Soraya", "Potter", "Foster", "Serian", "Morrow",
        "Sharma", "Amalia", "Bhatti", "Jordan", "Pollux", "Rivers", "Florez", "Aeneas", "Curtis", "Haldus", "Nakkar",
        "Madden", "Barnes", "Canada", "Luhman", "Conrad", "Atkins", "Yildun", "Wilcox", "Anubis", "Morton", "Struve",
        "Zamora", "Ixchel", "Future", "Khuyet", "Wadjet", "Conner", "Castor", "Cotton", "Moktar", "Dunlap", "Haliim",
        "Hardin", "Aurora", "Gagnon", "Arthur", "Talon", "Small", "Qasim", "Reyes", "Mason", "Yarih", "Lopez",
        "Kline", "Ashes", "Johns", "Logan", "Burke", "Kosso", "Pluto", "Simon", "Spica", "Kirby", "Allen", "Lange",
        "Sural", "Foley", "Malka", "River", "Hafer", "Dixon", "Chand", "Laval", "Nayar", "Ceres", "Neher", "Dolga",
        "Surya", "Motes", "Caden", "Keith", "Oxana", "Hyder", "Zhang", "Deneb", "Cador", "Joyce", "Gould", "Chase",
        "Cline", "Berry", "Sabra", "Lynch", "Batar", "Roper", "Olaru", "Borax", "Thrar", "Pekar", "Abasi", "Avior",
        "Thrax", "Haney", "Zosma", "Volta", "Mejia", "Cooke", "Noble", "Irene", "Cohen", "Gates", "Patel", "Zorya",
        "Huber", "Sprig", "Nares", "Sweet", "Jagar", "Klein", "Kubel", "Cacri", "Sears", "Henry", "Sedna", "Bruce",
        "Flynn", "Barry", "Crane", "Daraw", "Orion", "Osman", "Ginan", "Black", "Nixon", "Frost", "Homan", "Amaru",
        "Yacob", "Huang", "Alcor", "Wasat", "Gamal", "Perez", "Meyer", "Lihua", "Metro", "Bauer", "Anath", "Bulan",
        "Uther", "Soyuz", "Lapis", "Jules", "Algol", "Warad", "James", "Vantu", "Terry", "Ankaa", "Duffy", "Arise",
        "Titus", "Scott", "Tyson", "Wiley", "Pepin", "Pawar", "Fajar", "Miron", "Hakim", "Combs", "Aidan", "Hanta",
        "Tambo", "Rigel", "Ocean", "Garza", "Farah", "Tsuki", "Glass", "Nadya", "Innon", "Mimas", "Paolo", "Darg",
        "Zero", "Lina", "Nisa", "Sama", "Nour", "Otho", "Prax", "Mays", "Bada", "Lane", "Quch", "Jean", "Alom",
        "Nuuk", "Bayu", "Osta", "Goff", "Addo", "Hart", "Pace", "Noel", "Rozi", "Cook", "Kora", "Vach", "test",
        "Bell", "Rama", "Odom", "Fulu", "Boyd", "Tjon", "Kerr", "Nawa", "Bond", "Hope", "Ward", "Kidd", "Sora",
        "Enki", "Kuma", "Dust", "Wang", "Keir", "Isis", "Jang", "Linh", "Leon", "Wolf", "Frye", "Page", "Kemp",
        "Cain", "Adam", "Long", "Kebe", "Heze", "Buck", "Rada", "Lott", "Juma", "Inti", "Todd", "Kaya", "Lim", "Mir",
        "Yed", "Tir", "Qor", "Sur", "Ayu", "Ray", "Ngo", "Yau", "Nil", "Rye", "Osa", "Mia", "Zol", "Cox", "Nam",
        "Sen", "Nol", "Hom", "Ara", "Tyl", "Zhi", "Nat", "Guo", "Daw", "Interstellar",
        // ranks
        "guardHighDeputyExecutor", "deputyStarMarshal", "pilot",
        // voices
        "aristo",
        // weapon ids
        "neoferric_quadcoil", "pseudoparticle_jet", "inimical_emanation", "heavy_mass_driver", "disintegrator",
        "abyssal_glare", "resonatormrm", "rifttorpedo", "jackhammer", "gorgonpod", "gazer", "amsrm"
    )

    // Tier 2 (4379 bytes): expected saving per byte 0.01 .. 0.01
    private val tier2: List<String> = listOf(
        // hull ids
        "manticore_luddic_path", "lasher_luddic_church", "cerberus_luddic_path",
        // hullmod ids
        "high_frequency_attractor", "degraded_drive_field",
        // person names
        "Secondshift", "Chatterjee", "Spaceborn", "Patagonia", "Meridiana", "Galingale", "Eridania", "Hesperus",
        "Gaspesie", "Purslane", "Marshall", "Brumaire", "Daedalia", "Amazonis", "McDowell", "Montcalm", "Cabrakan",
        "Minerva", "Chandra", "Voronya", "Miranda", "Gilbert", "Richard", "Carlson", "Lasalle", "Douglas", "Yurania",
        "America", "Grafias", "William", "Canopus", "Abigail", "Olympia", "Francis", "Alvarez", "Stewart", "Raymond",
        "Shediac", "Kennedy", "Goodwin", "Lebanon", "Taygrta", "Sellers", "Cadence", "Orinoco", "Opeyemi", "Regulus",
        "Allison", "Ayodele", "Plateau", "Scripps", "Sirius", "Martin", "Molodo", "Phobos", "Hijack", "Hector",
        "Cooper", "Zaurak", "Fescue", "Walton", "Altair", "Ishtar", "Dudley", "Athena", "Stuart", "Sunday", "Spence",
        "Clarke", "Autumn", "Baloch", "Zaliya", "Second", "Lothar", "Tanzer", "Aeolis", "Friday", "Cybele", "Hyadum",
        "Gavril", "Julian", "Morgan", "Olivia", "Arslar", "Chaiya", "Dawson", "Gordon", "Riddle", "Bryan", "Itchy",
        "Casey", "Genat", "Helen", "Ansel", "Amina", "Drogo", "Alada", "Haley", "Thanh", "Miras", "Matar", "Megas",
        "Meier", "Miles", "Third", "Nzube", "Mesec", "Sofia", "Qadir", "Roman", "Riley", "Quell", "Dakka", "Kelly",
        "Lloyd", "Nunki", "Orcus", "Avery", "Moloy", "Lamb", "Kang", "Nasa", "Maud", "Vela", "Hess", "Emma", "Zhou",
        "Keid", "Uche", "Wade", "Kane", "Enyo", "Ulag", "Paul", "Zeus", "Kato", "Ryan", "Kali", "Mira", "Akna",
        "Luna", "John", "Eris", "Duke", "Rose", "Ares", "Sol", "May", "Yaw", "Nin", "Ali", "Zun", "Set", "Nox",
        "Roy", "Gao", "Min",
        // portraits
        "graphics/portraits/characters/horacio_caden.png", "graphics/portraits/characters/old_spacer01.png",
        "graphics/portraits/portrait_defactioned02.png", "graphics/portraits/portrait_defactioned00.png",
        "graphics/portraits/portrait_defactioned01.png", "graphics/portraits/characters/zig_captain.png",
        "graphics/portraits/characters/oxana_hyder.png", "graphics/portraits/portrait_corporate12.png",
        "graphics/portraits/portrait_corporate14.png", "graphics/portraits/portrait_corporate11.png",
        "graphics/portraits/portrait_corporate05.png", "graphics/portraits/portrait_luddic03.png",
        "graphics/portraits/portrait_pirate01.png", "graphics/portraits/portrait_pirate20.png",
        "graphics/portraits/portrait_luddic04.png", "graphics/portraits/portrait_diktat15.png",
        "graphics/portraits/portrait_diktat16.png", "graphics/portraits/portrait_diktat09.png",
        "graphics/portraits/portrait_league14.png", "graphics/portraits/portrait_league05.png",
        "graphics/portraits/portrait_luddic00.png", "graphics/portraits/portrait_luddic11.png",
        "graphics/portraits/portrait_luddic14.png", "graphics/portraits/portrait_league01.png",
        "graphics/portraits/portrait_diktat13.png", "graphics/portraits/portrait_league00.png",
        "graphics/portraits/portrait_diktat06.png", "graphics/portraits/portrait_luddic13.png",
        "graphics/portraits/portrait_pirate07.png", "graphics/portraits/portrait_luddic09.png",
        "graphics/portraits/portrait_diktat12.png", "graphics/portraits/portrait_luddic07.png",
        "graphics/portraits/portrait26.png", "graphics/portraits/portrait23.png",
        "graphics/portraits/portrait41.png", "graphics/portraits/portrait47.png",
        // ship names
        "Chicomoztoc Metroplex XV", "Tsiolkovsky's Vision", "Ultraviolet Sunrise", "Remember the Bismar",
        "Victory of Valhalla", "Fire of the Phoenix", "Hyperspace Dreaming", "Abundant Returning",
        "Victory at Jangala", "Laniakea Traveller", "Battle of Sindria", "Spirit of the XIV", "Iron Superiority",
        "Einstein's Demon", "Celestial Breeze", "Triumph of CXCIV", "Chalmecacihuilt", "Stellar Profits",
        "Hayot Ha Kodesh", "Chandragone Per", "Midgardian Cog", "Rose Celestial", "Chatelain Thea", "Antares Bright",
        "Sable-Cherjar", "Fleeting Moon", "Imbrium Flyer", "Cenn Cruaich", "Lucky Strike", "Stellar Rose",
        "Stellar Wind", "Lord Scarlet", "Quetzalcoatl", "Iridescence", "Stars Below", "Lightspeed+", "Bene Elohim",
        "Via Acubens", "Star Witch", "Titlacauan", "Imporcitor", "Agasthenes", "Chromolith", "Trade Wind",
        "Allegiance", "Unrivalled", "Solar Wind", "Xochipilli", "Fair Orbit", "Tenacious", "Hecaterus", "Astrovore",
        "Leviathan", "Annonaria", "Adsullata", "Aegisthus", "Securitas", "Convector", "Taikonaut", "Hlad-Zero",
        "Discordia", "Manawydan", "Delta-Max", "Iron Fist", "Santianna", "Mercurial", "Antevorte", "Kosmoskva",
        "Starfarer", "High Moon", "Space Dog", "Kosmonaut", "Blue Star", "Sparrowen", "Morgoyle", "Mag Mell",
        "Rhiannon", "Iota-Max", "Hersilia", "Patalena", "Ion Rose", "Favonius", "Lupercus", "Seraphim", "Cherubim",
        "Sol Long", "Vigilant", "Unshaken", "Azimyth", "Pelopia", "Zagreus", "Perdido", "Defiant", "Angitia",
        "Ophanim", "Unicorn", "Matton", "Achtan", "Damara", "Lucina", "Sphinx", "Glower", "Mannan", "Cu Roi",
        "Aeolus", "Virtus", "Gunnar", "Azazel", "Erelim", "Elohim", "Zenith", "Pequod", "Boann", "Macha", "Crete",
        "Inuus", "Balor", "Pooka", "Tyche", "Letum", "Comus", "Tengu", "Auror", "Juno", "Asp", "Uji",
        // variant tags
        "retain_smods_on_recovery"
    )

    // Tier 3 (4414 bytes): expected saving per byte 0.01 .. 0.01
    private val tier3: List<String> = listOf(
        // faction ids
        "derelict", "player", "ML_bounty",
        // fighter wing ids
        "claw_wing",
        // fleet type names / fleet names
        "Nexus (Damaged)", "Fast Picket", "Privateer", "Scavenger",
        // hull ids
        "afflictor_d_pirates", "brawler_tritachyon", "eradicator_pirates", "gremlin_d_pirates", "station2_hightech",
        "hound_luddic_path", "station2_midline", "kite_luddic_path", "venture_pather", "brawler_pather",
        "shrike_pirates", "kite_original", "mudskipper", "onslaught", "dominator", "colossus2", "venture_p",
        "ziggurat", "revenant", "sunder_d", "mercury", "rampart", "radiant", "nebula", "sunder", "anubis", "sentry",
        "astral", "apogee", "crig", "nova",
        // hullmod ids
        "high_scatter_amp", "degraded_engines", "comp_structure", "shield_shunt", "comp_storage", "phase_anchor",
        // person names
        "Gilgamesh", "Wednesday", "Symbiote", "Tenthgen", "Chimdalu", "Gonzalez", "Lindsay", "Mercury", "Eugenia",
        "Cameron", "Norton", "Saturn", "Ashley", "Makeda", "Hayden", "Holden", "Tanner", "Mekong", "Kohler",
        "Fourth", "Esther", "George", "Hasper", "Argyre", "Fauzi", "Kyugu", "Three", "Grant", "Freya", "Venus",
        "Chaac", "Lewis", "Neva", "Mars", "Ursa", "Rich", "Dale", "Lara", "Anna", "Zada", "Gill", "Two", "Ard",
        // personalities
        "timid",
        // portraits
        "graphics/portraits/portrait_generic_grayscale.png", "graphics/portraits/portrait_defactioned04.png",
        "graphics/portraits/portrait_corporate13.png", "graphics/portraits/portrait_corporate03.png",
        "graphics/portraits/portrait_corporate06.png", "graphics/portraits/portrait_corporate08.png",
        "graphics/portraits/portrait_corporate02.png", "graphics/portraits/portrait_mercenary05.png",
        "graphics/portraits/portrait_hegemony06.png", "graphics/portraits/portrait_pirate04.png",
        "graphics/portraits/portrait_league03.png", "graphics/portraits/portrait_diktat10.png",
        "graphics/portraits/portrait_diktat14.png", "graphics/portraits/portrait_league08.png",
        "graphics/portraits/portrait_league10.png", "graphics/portraits/portrait_league11.png",
        "graphics/portraits/portrait_luddic06.png", "graphics/portraits/portrait_league13.png",
        "graphics/portraits/portrait_league06.png", "graphics/portraits/portrait_ai3b.png",
        "graphics/portraits/portrait_ai1b.png", "graphics/portraits/portrait_ai2.png",
        "graphics/portraits/portrait_ai1.png", "graphics/portraits/portrait24.png",
        "graphics/portraits/portrait49.png", "graphics/portraits/threat.png",
        "graphics/portraits/portrait_defactioned05.png", "graphics/portraits/portrait_defactioned07.png",
        "graphics/portraits/portrait_defactioned03.png", "graphics/portraits/portrait_defactioned06.png",
        "graphics/portraits/portrait_corporate09.png", "graphics/portraits/portrait_corporate01.png",
        "graphics/portraits/portrait_corporate10.png", "graphics/portraits/portrait_corporate07.png",
        "graphics/portraits/portrait_hegemony11.png", "graphics/portraits/portrait_hegemony17.png",
        "graphics/portraits/portrait_hegemony16.png", "graphics/portraits/portrait_hegemony14.png",
        "graphics/portraits/portrait_luddic10.png", "graphics/portraits/portrait_pirate15.png",
        "graphics/portraits/portrait_pirate13.png", "graphics/portraits/portrait_pirate05.png",
        "graphics/portraits/portrait_pirate06.png", "graphics/portraits/portrait_pirate03.png",
        "graphics/portraits/portrait_pirate11.png", "graphics/portraits/portrait_pirate10.png",
        "graphics/portraits/portrait_pirate22.png", "graphics/portraits/portrait_diktat11.png",
        "graphics/portraits/portrait_league12.png", "graphics/portraits/portrait_diktat05.png",
        "graphics/portraits/portrait_diktat04.png", "graphics/portraits/portrait_league09.png",
        "graphics/portraits/portrait_pirate09.png", "graphics/portraits/portrait_luddic16.png",
        "graphics/portraits/portrait_league04.png", "graphics/portraits/portrait_luddic01.png",
        "graphics/portraits/portrait_league02.png", "graphics/portraits/portrait_luddic15.png",
        "graphics/portraits/portrait_pirate18.png", "graphics/portraits/portrait46.png",
        "graphics/portraits/portrait17.png", "graphics/portraits/portrait38.png",
        "graphics/portraits/portrait18.png", "graphics/portraits/portrait48.png",
        "graphics/portraits/portrait44.png", "graphics/portraits/portrait_mercenary04.png",
        "graphics/portraits/portrait_hegemony13.png", "graphics/portraits/portrait_hegemony03.png",
        // posts
        "mercenary",
        // ship name prefixes
        "10000 ",
        // ship names
        "Never Again Chi Pegasi", "Victory of Chicomoztoc", "Prize of High Orbit", "Interstitial Breeze",
        "Prince of the Stars", "Don't Stop Until C", "Arising Hyperbolic", "Old Earth Or Bust", "Pride of Achernar",
        "Queen of Jupiter", "Plentiful Volume", "Korolev Dreaming", "Aloft Parabolic", "Tongoenabiagus",
        "Space Raptor", "Caloris Born", "Rigel Flyer", "Legionnaire", "Bright Burn", "Spacefarer", "Cuchulainn",
        "Starfinder", "Faustulus", "Black Sun", "Camaxtli", "Caeculus", "Anticlea", "Cosmosis", "Jupiter", "Amulius",
        "Victory", "Azimuth", "Meteor", "Mextli", "Fenris", "Coeurl", "Pietas", "Edain",
        // variant display names
        "Hellbore", "Outdated", "Damaged", "Pirate", "DEM",
        // voices
        "scientist",
        // weapon ids
        "annihilatorpod", "mininglaser", "dragonpod", "tpc",
        // weapon slot ids
        "WS 031"
    )

    // Tier 4 (4342 bytes): expected saving per byte 0.01 .. 0.04
    private val tier4: List<String> = listOf(
        // faction ids
        "sindrian_diktat", "luddic_path", "luddic_church",
        // fighter wing ids
        "perdition_wing", "warthog_wing", "sarissa_wing", "dagger_wing", "xyphos_wing", "longbow_wing", "cobra_wing",
        // fleet type names / fleet names
        "Mercenary Patrol", "Patrol", "Sacred Protectors", "Mercantile Convoy", "Mercenary Company",
        "Commerce Raiders", "Inspection Fleet", "Luddic Pilgrims", "Investigators", "Relief Convoy", "Liner Convoy",
        "Grand Armada", "Supply Fleet", "Task Force", "Prospector", "Enforcer", "Rogue Miner Force",
        // hull ids
        "gremlin_luddic_path", "station1_hightech", "vanguard_pirates", "wolf_d_pirates", "hound_hegemony",
        "hammerhead_LG", "centurion_LG", "prometheus2", "brawler_LG", "cerberus_d", "legion_xiv", "falcon_LG",
        "eagle_xiv", "wayfarer", "eagle_LG", "venture", "odyssey", "drover", "aurora", "scarab", "atlas2", "fury",
        "module_hightech_citadel", "module_hightech_armour", "module_decor_midline2", "module_hightech_strut",
        "station3_midline", "module_armour1", "manticore", "colossus3", "afflictor", "executor", "champion",
        "invictus", "monitor", "falcon", "medusa", "tarsus", "gemini", "eagle", "doom",
        "module_hightech_attack_minelayer", "module_hightech_attack", "station3_hightech", "mule_d_pirates",
        "onslaught_xiv", "dominator_xiv", "enforcer_xiv", "mudskipper2", "retribution", "falcon_xiv", "hammerhead",
        "eradicator", "sunder_LG", "harbinger", "falcon_p", "conquest", "gremlin", "grendel", "tempest", "pegasus",
        "paragon", "hound_d", "gryphon", "heron", "shade", "mora", "module_midline_bastion", "module_decor_midline1",
        "manticore_pirates", "station1_midline", "wolf_hegemony", "hyperion", "station3", "buffalo", "shrike",
        // hullmod ids
        "militarized_subsystems", "ballistic_rangefinder", "increased_maintenance", "degraded_life_support",
        "efficiency_overhaul", "surveying_equipment", "expanded_deck_crew", "operations_center", "solar_shielding",
        "adaptiveshields", "hiressensors", "faulty_grid", "faulty_auto", "ecm", "defensive_targeting_array",
        "glitched_sensors", "degraded_shields", "comp_armor", "nav_relay", "dedicated_targeting_core",
        "extendedshieldemitter", "fragile_subsystems", "augmentedengines", "erratic_injector", "adaptive_coils",
        "damaged_mounts", "frontemitter", "frontshield",
        // person names
        "Artemis", "Pyramus", "Titania", "Whitney", "Charon", "Europa", "Monday", "Deimos", "Quinn", "Reese",
        "Alice", "Allat", "Lynn", "Kim", "Ganymede", "Elysium", "Eunomia", "Elektra", "Shannon",
        // personalities
        "cautious",
        // portraits
        "graphics/portraits/portrait_hegemony05.png", "graphics/portraits/portrait_hegemony15.png",
        "graphics/portraits/portrait_hegemony07.png", "graphics/portraits/portrait_hegemony09.png",
        "graphics/portraits/portrait_pirate19.png", "graphics/portraits/portrait_pirate14.png",
        "graphics/portraits/portrait_diktat02.png", "graphics/portraits/portrait_pirate17.png",
        "graphics/portraits/portrait_luddic12.png", "graphics/portraits/portrait39.png",
        "graphics/portraits/portrait20.png", "graphics/portraits/portrait43.png",
        "graphics/portraits/portrait25.png", "graphics/portraits/portrait13.png",
        "graphics/portraits/portrait_mercenary03.png", "graphics/portraits/portrait_corporate04.png",
        "graphics/portraits/portrait_mercenary07.png", "graphics/portraits/portrait_hegemony08.png",
        "graphics/portraits/portrait_pirate21.png", "graphics/portraits/portrait_pirate12.png",
        "graphics/portraits/portrait_pirate02.png", "graphics/portraits/portrait_luddic08.png",
        "graphics/portraits/portrait_luddic05.png", "graphics/portraits/portrait_league07.png",
        "graphics/portraits/portrait_diktat07.png", "graphics/portraits/portrait_diktat08.png",
        "graphics/portraits/portrait_diktat03.png", "graphics/portraits/portrait_diktat01.png",
        "graphics/portraits/portrait_luddic02.png", "graphics/portraits/portrait_pirate16.png",
        "graphics/portraits/portrait45.png", "graphics/portraits/portrait35.png",
        "graphics/portraits/portrait21.png", "graphics/portraits/portrait_hegemony01.png",
        "graphics/portraits/portrait_hegemony12.png", "graphics/portraits/portrait_hegemony04.png",
        "graphics/portraits/portrait28.png", "graphics/portraits/portrait32.png",
        "graphics/portraits/portrait27.png",
        // ranks
        "spaceAdmiral",
        // ship names
        "10000 Lightyears", "Aldebaran Rising", "Memory of Deneb", "Kardashevian", "Comet's Tail", "Golden Glow",
        "Bon Comet", "Starflyer", "Comet", "Nova", "Apologies To Goddard", "Third Moon Of Mars", "Orphan of Earth",
        "Forgive Us Enos", "Gagarin's Dream", "Tachyon And On", "Caspian Sky", "Nightrunner", "Stardriven",
        "Starhawk",
        // skill ids
        "bulk_transport", "phase_corps", "sensors", "derelict_contingent", "officer_management", "best_of_the_best",
        "wolfpack_tactics",
        // variant display names
        "Experimental", "Command", "EMP Hardened", "Fire Support", "Shielded",
        // variant tags
        "always_recoverable", "skip_for_default_hull_modules", "consistent_weapon_drops",
        // voices
        "faithful",
        // weapon ids
        "kineticblaster", "pilum_large", "heavyburst", "cyclone", "harpoon_single", "gigacannon", "hammerrack",
        "gazerpod", "dragon", "hydra", "miningblaster",
        // weapon slot ids
        "WS 029", "WS 025", "WS 028"
    )

    // Tier 5 (4370 bytes): expected saving per byte 0.04 .. 0.93
    private val tier5: List<String> = listOf(
        // ai cores
        "alpha_core", "beta_core", "gamma_core",
        // faction ids
        "tritachyon", "persean", "neutral", "adversary", "remnant", "pirates", "hegemony", "independent",
        // fighter wing ids
        "gladius_wing", "talon_wing", "trident_wing", "flash_wing", "thunder_wing", "hoplon_wing", "spark_wing",
        "lux_wing", "piranha_wing", "broadsword_wing",
        // fleet type names / fleet names
        "Smuggler", "Nexus",
        // hull ids
        "mule", "module_hightech_hangar", "module_hightech_shield", "cerberus_d_pirates", "module_blocker2c",
        "hound_d_pirates", "prometheus", "vanguard", "cerberus", "colossus", "enforcer", "brawler", "condor",
        "hermes", "atlas", "omen", "centurion", "station2", "buffalo2", "phaeton", "kite_hegemony", "kite_pirates",
        "wolf", "remnant_shield2", "module_midline_broadside", "module_midline_support", "module_blocker2b",
        "vigilance", "lasher", "dram", "kite", "remnant_weapon_platform2", "remnant_weapon_platform1",
        "module_citadel1_lowtech", "module_bastion1_lowtech", "remnant_station2", "remnant_hangar1",
        "remnant_armour3", "module_armour4", "shepherd", "station1", "module_armour2", "hound",
        "module_large2_lowtech", "module_large1_lowtech", "module_armour3",
        // hullmod ids
        "converted_hangar", "autorepair", "magazines", "stabilizedshieldemitter", "advancedoptics",
        "safetyoverrides", "pointdefenseai", "hardened_subsystems", "fluxcoil", "unstable_injector",
        "hardenedshieldemitter", "missile_autoloader", "reinforcedhull", "turretgyros", "eccm", "insulatedengine",
        "blast_doors", "heavyarmor", "armoredweapons", "fluxbreakers", "targetingunit", "fluxdistributor",
        "missleracks", "auxiliarythrusters",
        // person memory keys
        "captain_unremovable", "voice",
        // person names
        "Chiron", "Themis", "Uranus", "Hektor", "Anurak", "Betony", "Four", "Heka", "Thor", "Day", "Kleopatra",
        "Callisto", "Lindsey", "Triton", "Oberon", "Titan", "Hebe", "Dysnomia", "Thisbe", "Nessus", "Eulalia",
        "Gula", "Olympus", "Psyche", "Urania", "Hygiea", "Alpha Core",
        // personalities
        "aggressive", "reckless", "steady",
        // portraits
        "graphics/portraits/portrait_mercenary06.png", "graphics/portraits/portrait_hegemony10.png",
        "graphics/portraits/portrait_pirate08.png", "graphics/portraits/portrait36.png",
        "graphics/portraits/portrait40.png", "graphics/portraits/portrait31.png",
        "graphics/portraits/portrait29.png", "graphics/portraits/portrait12.png",
        "graphics/portraits/portrait14.png", "graphics/portraits/portrait30.png",
        "graphics/portraits/portrait_mercenary02.png", "graphics/portraits/portrait_hegemony02.png",
        "graphics/portraits/portrait16.png", "graphics/portraits/portrait_mercenary01.png",
        "graphics/portraits/portrait33.png", "graphics/portraits/portrait42.png",
        "graphics/portraits/portrait_mercenary08.png", "graphics/portraits/portrait37.png",
        "graphics/portraits/portrait34.png", "graphics/portraits/portrait22.png",
        "graphics/portraits/portrait15.png", "graphics/portraits/portrait_ai2b.png",
        // posts
        "patrolCommander", "fleetCommander",
        // ranks
        "spaceCaptain", "spaceCommander",
        // ship name prefixes
        "TTDS ", "CGR ", "DSS ", "TTS ", "SDS ", "PLS ", "LGS ", "HSS ", "ISS ", "ADS ",
        // ship names
        "Laika", "Dark Matters", "Blueshift", "Sirius Express",
        // skill ids
        "containment_procedures", "fighter_uplink", "field_repairs", "navigation", "hull_restoration",
        "cybernetic_augmentation", "support_doctrine", "carrier_group", "electronic_warfare", "tactical_drills",
        "officer_training", "flux_regulation", "coordinated_maneuvers", "energy_weapon_mastery", "systems_expertise",
        "point_defense", "missile_specialization", "ballistic_mastery", "crew_training", "ordnance_expert",
        "field_modulation", "combat_endurance", "impact_mitigation", "polarized_armor", "damage_control",
        "target_analysis", "helmsmanship", "gunnery_implants",
        // variant display names
        "Super", "Point Defense", "Interceptor", "Balanced", "Raider", "Escort", "Battlestation", "Overdriven",
        "Light", "Close Support", "Star Fortress", "Elite", "Frontier", "Attack", "Orbital Station", "Assault",
        "Support", "Strike", "Standard",
        // variant tags
        "variant_always_retain_smods_on_salvage", "no_autofit",
        // voices
        "pather", "villain", "spacer", "business", "official", "soldier",
        // weapon ids
        "irautolance", "atropos", "locust", "breachpod", "heavymg", "plasma", "gorgon", "lightmortar", "heavymortar",
        "devastator", "hurricane", "shredder", "tachyonlance", "railgun", "heavyblaster", "heavyneedler", "taclaser",
        "reaper", "breach", "multineedler", "guardian", "arbalest", "hephag", "hammer", "amblaster", "ionbeam",
        "annihilator", "phasebeam", "harpoonpod", "irpulse", "autopulse", "lightneedler", "flak", "hil", "sabot",
        "lightdualac", "lrpdlaser", "lightac", "lightag", "ionpulser", "squall", "pdburst", "heavyac",
        "gravitonbeam", "pulselaser", "gauss", "mjolnir", "salamanderpod", "heavymauler", "ioncannon", "chaingun",
        "heatseeker", "typhoon", "phasecl", "harpoon", "hellbore", "lightmg", "mark9", "dualflak", "sabotpod",
        "pdlaser", "hveldriver", "lightdualmg", "swarmer", "pilum", "vulcan",
        // weapon slot ids
        "WS 033", "WS 035", "WS 037", "WS 040", "WS 034", "WS 032", "WS 038", "WS 030", "WS 039", "WS 036", "WS 027",
        "WS 022", "WS 024", "WS 026", "WS 019", "WS 017", "WS 023", "WS 014", "WS 021", "WS 018", "WS 020", "WS 016",
        "WS 015", "WS 013", "WS 012", "WS 011", "WS 010", "WS 009", "WS 000", "WS 008", "WS 007", "WS 006", "WS 005",
        "WS 004", "WS 003", "WS 002", "WS 001"
    )

    val v2: ByteArray = build(pairs + tier0 + tier1 + tier2 + tier3 + tier4 + tier5)
        .also { check(it.size <= 32768) { "Dictionary V2 exceeds 32 KB: ${it.size}" } }
}