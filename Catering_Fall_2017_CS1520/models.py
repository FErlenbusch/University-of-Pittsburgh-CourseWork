from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class User(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	username = db.Column(db.String(32), index=True, unique=True, nullable=False)
	user_type = db.Column(db.String(1), nullable=False)
	first = db.Column(db.String(15), nullable=False)
	last = db.Column(db.String(15), nullable=False)
	pw_hash = db.Column(db.String(128), nullable=False)

	events = db.relationship('Event', backref='client', lazy=True)
	job = db.relationship('Job', backref='working', lazy=True)

	def __init__(self, username, user_type, first, last, pw_hash):
		self.username = username
		self.user_type = user_type
		self.first = first
		self.last = last
		self.pw_hash = pw_hash

	def __repr__(self):
		return '<User {}>'.format(self.username)

class Event(db.Model):
	event_id = db.Column(db.Integer, primary_key=True)
	client_id = db.Column(db.Integer, db.ForeignKey('user.id'))
	event_date = db.Column(db.String(10), unique=True, nullable=False)

	job = db.relationship('Job', backref='workers', lazy=True)

	def __init__(self, client_id, event_date):
		self.client_id = client_id
		self.event_date = event_date

	def __repr__(self):
		return '<Event {}>'.format(self.event_id)

class Job(db.Model):
	job_id = db.Column(db.Integer, primary_key=True)
	job_event = db.Column(db.String(10), db.ForeignKey('event.event_date'))
	job_worker = db.Column(db.Integer, db.ForeignKey('user.id'))

	def __init__(self, job_event, job_worker):
		self.job_event = job_event
		self.job_worker = job_worker

	def __repr__(self):
		return '<Job {}>'.format(self.job_event)

