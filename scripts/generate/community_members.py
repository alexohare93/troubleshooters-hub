import random

data = []

for i in range(100):
    data.append((random.randrange(1, 101), random.randrange(1, 102)))

# remove duplicate entries
data = list(dict.fromkeys(data))

sql = "INSERT INTO CommunityMembers (CommunityId, UserId) VALUES ({communityId}, {userId});\n"
path = "../mock_data/community_members.sql"

with open(path, mode="w", newline="", encoding="utf-8") as file:
    file.write("BEGIN TRANSACTION;\n")
    for d in data:
        file.write(sql.format(communityId=d[0], userId=d[1]))
    file.write("COMMIT;")
