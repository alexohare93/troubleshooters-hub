import random

data = []

for i in range(100):
    data.append([random.randrange(1, 101), random.randrange(1, 102)])

sql = "INSERT INTO EventAttendees (EventId, UserId) VALUES ({eventId}, {userId});\n"
path = "../mock_data/event_attendees.sql"

with open(path, mode="w", newline="", encoding="utf-8") as file:
    for d in data:
        file.write(sql.format(eventId=d[0], userId=d[1]))
