import os
import collections

from flask import Flask
from flask import render_template

from flask_restful import reqparse, Api, Resource, fields, marshal_with

from datetime import datetime

from models import db, Category, Purchase

app = Flask(__name__)
api = Api(app)

DEBUG = True
SECRET_KEY = 'development key'

SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(app.root_path, 'budget.db')
SQLALCHEMY_TRACK_MODIFICATIONS = False

app.config.from_object(__name__)

db.init_app(app)

category_fields = {
	'cat_id':		fields.Integer,
	'name':			fields.String,
	'amount': 		fields.Float
}

purchase_fields = {
	'pur_id':		fields.Integer,
	'description':	fields.String,
	'amount': 		fields.Float,
	'category':		fields.Integer,
	'date':			fields.String,
}

cat_parser = reqparse.RequestParser()
cat_parser.add_argument('name')
cat_parser.add_argument('amount')

pur_parser = reqparse.RequestParser()
pur_parser.add_argument('description')
pur_parser.add_argument('amount')
pur_parser.add_argument('category')
pur_parser.add_argument('date')


class CategoryDao(object):
	def __init__(self, cat_id, name, amount):
		self.cat_id = cat_id
		self.name = name
		self.amount = amount


class PurchaseDao(object):
	def __init__(self, pur_id, description, amount, category, date):
		self.pur_id = pur_id
		self.description = description
		self.amount = amount
		self.category = category
		self.date = date


class Purchases(Resource):
	@marshal_with(purchase_fields)
	def get(self):
		pur_all = Purchase.query.order_by(Purchase.date).all()
		pur_new = []

		for pur in pur_all:
			pur_new.append(PurchaseDao(pur_id=pur.pur_id, description=pur.description,
				amount=pur.amount, category=pur.category, date=pur.date))

		return pur_new, 200

	@marshal_with(purchase_fields)
	def post(self):
		args = pur_parser.parse_args()
		db.session.add(Purchase(description=args['description'],
			amount=args['amount'], category=args['category'],
			date=datetime.strptime(args['date'], '%Y-%m-%d').date()))
		db.session.commit()
		
		pur_new = Purchase.query.filter_by(description=args['description'],
			amount=args['amount'], category=args['category'],
			date=args['date']).order_by(Purchase.pur_id.desc()).first()
		pur_new = PurchaseDao(pur_id=pur_new.pur_id, description=pur_new.description,
			amount=pur_new.amount, category=pur_new.category, date=pur_new.date)

		return pur_new, 201

	def put(self):
		args = pur_parser.parse_args()
		pur = Purchase.query.filter_by(pur_id=args['description']).first()
		
		pur.amount = args['amount']
		pur.category = args['category']
		pur.date = datetime.strptime(args['date'], '%Y-%m-%d').date()
		db.session.commit()

		return 'Modify Purchase Successful', 201

	def delete(self):
		args = pur_parser.parse_args()
		pur = Purchase.query.filter_by(pur_id=args['description']).first()

		db.session.delete(pur)
		db.session.commit()

		return 'Delete Purchase Successful', 204


class Categories(Resource):
	@marshal_with(category_fields)
	def get(self):
		cat_all = Category.query.all()
		cat_new = []

		for cat in cat_all:
			cat_new.append(CategoryDao(cat_id=cat.cat_id, name=cat.name,
				amount=cat.amount))

		return cat_new, 200

	@marshal_with(category_fields)
	def post(self):
		args = cat_parser.parse_args()
		db.session.add(Category(name=args['name'], amount=args['amount']))
		db.session.commit()
		
		cat_new = Category.query.filter_by(name=args['name']).first()
		cat_new = CategoryDao(cat_id=cat_new.cat_id, name=cat_new.name,
			amount=cat_new.amount)

		return cat_new, 201

	def put(self):
		args = cat_parser.parse_args()
		cat = Category.query.filter_by(cat_id=args['name']).first()
		
		cat.amount = args['amount']
		db.session.commit()

		return 'Modify Category Successful', 201

	def delete(self):
		args = cat_parser.parse_args()
		purs = Purchase.query.filter_by(category=args['name']).all()
		cat = Category.query.filter_by(cat_id=args['name']).first()

		for pur in purs:
			pur.category = 1
			db.session.commit()

		db.session.delete(cat)
		db.session.commit()

		return 'Delete Category Successful', 204


@app.cli.command('initdb')
def initdb_command():
	db.create_all()
	db.session.add(Category(name="Uncategorized",
		amount="0.00"))
	db.session.commit()
	print('Initialized the database.')


@app.route('/')
def index():
	return render_template('index.html', title="Home")


api.add_resource(Categories, '/cats')
api.add_resource(Purchases, '/purchases')


if __name__ == "__main__":
	app.run(host="0.0.0.0", debug=True, threaded=True)