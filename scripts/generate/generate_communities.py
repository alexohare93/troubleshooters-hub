import random


names = [
    "Harmonic Horizons",
    "Sonic Syndicate",
    "Rhythm Reverie",
    "The Groove Circle",
    "The Melody Makers",
    "Beatwave Collective",
    "Crescendo Crew",
    "Soul Sync Society",
    "Acoustic Alliance",
    "Soundwave Junction",
    "Echo Ensemble",
    "The Jazz Haven",
    "Tempo Tribe",
    "Fusion Frequencies",
    "Vibe Vault",
    "The Keynote Collective",
    "Serenade Society",
    "The Beatnik Brigade",
    "Chord Connectors",
    "Sonic Bloom",
    "Melody Caravan",
    "Cosmic Choir",
    "Bassline Brotherhood",
    "Pulse Pioneers",
    "Rhythm Nomads",
    "The Acoustic Ambassadors",
    "Syncopation Syndicate",
    "Treble Trails",
    "Tone Tinkerers",
    "Harmony Hangout",
    "Groove Architects",
    "Songcraft Society",
    "Frequency Frontier",
    "Dreamtone Collective",
    "Riff & Rhythm Assembly",
    "Vinyl Vanguards",
    "The Tempo Travelers",
    "Sound Striders",
    "Harmonic Pilgrims",
    "Lyrical Legends",
    "The Chord Explorers",
    "Synthwave Sanctuary",
    "The Beat Bazaar",
    "Echo Chamber Union",
    "Sonic Architects",
    "The Crescendo Coalition",
    "Serenade Seekers",
    "Songbird Syndicate",
    "Groove Nomads",
    "Frequency Friends",
    "Tone Explorers",
    "Bassline Bound",
    "Melody Nexus",
    "Jazz Odyssey",
    "Rhythm & Roots Collective",
    "The Harmonic Hub",
    "Synthwave Trailblazers",
    "The Beat Haven",
    "Chord Caravan",
    "Sonic Fusion Society",
    "Serenade Travelers",
    "Sound Sphere Assembly",
    "The Vibe Collective",
    "Acoustic Elevation",
    "Frequency Nomads",
    "The Tempo Syndicate",
    "Harmonic Odyssey",
    "Bassline Explorers",
    "Groove Sphere",
    "Tone Wanderers",
    "Riff Collective",
    "Serenade Junction",
    "The Jazz Syndicate",
    "Sonic Expedition",
    "The Groove Nexus",
    "Melodic Adventures",
    "Pulse Explorers",
    "The Crescendo Syndicate",
    "Chord & Cadence Crew",
    "Frequency Travelers",
    "Echo Odyssey",
    "Acoustic Navigators",
    "Harmonic Explorers",
    "Groovebound Collective",
    "Bassline Brotherhood",
    "The Melody Travelers",
    "Rhythm Realm",
    "Jazz Voyager Society",
    "The Tone Caravan",
    "Sound Haven Society",
    "Pulse Patterns Collective",
    "Echo & Rhythm Assembly",
    "The Groove Garden",
    "Acoustic Chronicles",
    "Melody Wanderers",
    "Tempo Travelers Union",
    "Sonic Wavefront",
    "Serenade Society Junction",
    "Harmonic Hikers",
    "Soundwave Explorers"
]


# List of potential genres
genres = [
    "Jazz", "Classical", "Hip-Hop", "Electronic", "Folk",
    "Blues", "Rock", "Pop", "Reggae", "Indie",
    "Country", "Metal", "Synthwave", "R&B", "World Music"
]

# List of mock descriptions fitting musical communities
descriptions = [
    "A vibrant group celebrating the roots and rhythms of {genre}.",
    "Where musicians explore innovative {genre} fusion projects.",
    "Dedicated to preserving the traditions and sounds of {genre}.",
    "An experimental community redefining the future of {genre}.",
    "Connecting global fans and creators of {genre}.",
    "Monthly jam sessions to dive deep into {genre} vibes.",
    "A hub for emerging artists in the {genre} scene.",
    "Focused on collaborative projects that push the boundaries of {genre}.",
    "Join us for discussions on the evolution of {genre}.",
    "Live performances and workshops for {genre} enthusiasts.",
]

# Generate the mock data
data = []
for i in range(100):
    name = names[i]
    genre = random.choice(genres)
    description = random.choice(descriptions).format(genre=genre)
    data.append([name, genre, description])

# Save as a CSV file
output_path = "../mock_data/communities.sql"
sql = "INSERT INTO Communities (Name, Genre, Description) VALUES ('{name}', '{genre}', '{description}');\n"

with open(output_path, mode="w", newline="", encoding="utf-8") as file:
    for d in data:
        file.write(sql.format(name=d[0], genre=d[1], description=d[2]))
