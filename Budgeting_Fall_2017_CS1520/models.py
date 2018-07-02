from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()


class Category(db.Model):
	cat_id = db.Column(db.Integer, primary_key=True)
	name = db.Column(db.String(24), nullable=False)
	amount = db.Column(db.Float, nullable=False)

	purchases = db.relationship('Purchase', backref='thingsPurchased', lazy=True)
	
	def __init__(self, name, amount):
		self.name = name
		self.amount = amount


class Purchase(db.Model):
	pur_id = db.Column(db.Integer, primary_key=True)
	description = db.Column(db.String(1024), nullable=False)
	amount = db.Column(db.Float, nullable=False)
	category = db.Column(db.Integer, db.ForeignKey('category.cat_id'),
		nullable=True)
	date = db.Column(db.Date, nullable=False)
	
	def __init__(self, description, amount, category, date):
		self.description = description
		self.amount = amount
		self.category = category
		self.date = date
