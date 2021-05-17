from flask import Flask, render_template
from opentok import OpenTok
import os
from pymongo import MongoClient

app = Flask(__name__)
api_key = '47225844'
secret = 'b5ec49e40ee3ab9bad77281a25d7609b5e544e95'
opentok = OpenTok(api_key, secret)

cliente = MongoClient("mongodb+srv://vonage-db:159487@cluster0.cooxp.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
bd = cliente['vonage-db']
rooms_table = bd.rooms_table

def salvarRoom(room_name, session_id):
    res = rooms_table.find_one({"room_name": room_name})
    if(res != None):
        rooms_table.update_one({"room_name": room_name}, { "$set": {"room_name":room_name, "session_id": session_id}})
    else:
        rooms_table.insert_one({"room_name": room_name, "session_id":session_id}).inserted_id

def getRoom(room_name):
    return rooms_table.find_one({"room_name": room_name})

@app.route('/generate_room/<room_name>', methods=['POST'])
def generate_room(room_name):
    session = opentok.create_session()
    session_id = session.session_id
    salvarRoom(room_name, session_id)
    return {"room_name":room_name , "session_id":session_id}

@app.route('/join_room/<room_name>', methods=['GET'])
def join_room(room_name):
    key = api_key
    result = getRoom(room_name)
    if(result != None):
        session_id = result['session_id']
        token = opentok.generate_token(session_id)
        return {"api_key": key, "session_id":session_id, "token":token}
    return {"Error": "Room não encontrada."}

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    app.run(host='0.0.0.0', port=port)