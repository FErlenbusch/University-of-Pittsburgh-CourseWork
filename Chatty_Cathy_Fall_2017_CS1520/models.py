from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()


class User(db.Model):
	user_id = db.Column(db.Integer, primary_key=True)
	username = db.Column(db.String(16), index=True, unique=True, nullable=False)
	email = db.Column(db.String(32), unique=True, nullable=False)
	pw_hash = db.Column(db.String(64), nullable=False)
	rooms = db.relationship('Chatroom', backref='chatrooms', lazy=True)
	messages = db.relationship('Message', backref='published', lazy=True)

	def __init__(self, username, email, pw_hash):
		self.username = username
		self.email = email
		self.pw_hash = pw_hash
		

class Chatroom(db.Model):
	chat_id = db.Column(db.Integer, primary_key=True)
	name = db.Column(db.String(24), unique=True, nullable=False)
	owner = db.Column(db.String(16), db.ForeignKey('user.username'))

	messages = db.relationship('Message', backref='messages', lazy=True)
	
	def __init__(self, name, owner):
		self.name = name
		self.owner = owner
		

class Message(db.Model):
	msg_id = db.Column(db.Integer, primary_key=True)
	author = db.Column(db.String(16), db.ForeignKey('user.username'),
		nullable=False)
	contents = db.Column(db.String(1024), nullable=False)
	chatroom = db.Column(db.Integer, db.ForeignKey('chatroom.chat_id'),
		nullable=False)
	time = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
	
	def __init__(self, author, contents, chatroom):
		self.author = author
		self.contents = contents
		self.chatroom = chatroom

