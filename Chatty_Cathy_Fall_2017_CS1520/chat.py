import os

from flask import Flask, request, session, url_for, redirect
from flask import render_template, abort, g, flash
from flask_restful import reqparse, Api, Resource, fields, marshal_with

from werkzeug import check_password_hash, generate_password_hash

from flask_wtf import FlaskForm
from wtforms import SubmitField, HiddenField

from models import db, User, Chatroom, Message

app = Flask(__name__)
api = Api(app)

DEBUG = True
SECRET_KEY = 'development key'

SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(app.root_path, 'chat.db')
SQLALCHEMY_TRACK_MODIFICATIONS = False

app.config.from_object(__name__)

db.init_app(app)

resource_fields = {
    'contents':		fields.String,
    'author':		fields.String,
    'time': 		fields.Float}

parser = reqparse.RequestParser()
parser.add_argument('msg')


class MsgDao(object):
    def __init__(self, msg_id, contents, author, time):
        self.msg_id = msg_id
        self.contents = contents
        self.author = author
        self.time = time

        # This field will not be sent in the response
        self.status = 'active'


class Chatrooms(Resource):
	@marshal_with(resource_fields)
	def get(self, chat_id, msg_time):
		if Chatroom.query.filter_by(chat_id=chat_id).first() is None:
			abort(404, message="Chatroom {} doesn't exist in chatroom".format(chat_id))

		msg_all = Message.query.filter_by(chatroom=chat_id).all()
		msg_new = []

		if msg_time is None:
			for msg in msg_all:
				msg_new.append(MsgDao(msg_id=msg.msg_id, contents=msg.contents,
					author=msg.author, time=msg.time.timestamp()))
		else:
			for msg in msg_all:
				if msg.time.timestamp() > float(msg_time):
					msg_new.append(MsgDao(msg_id=msg.msg_id, contents=msg.contents,
					author=msg.author, time=msg.time.timestamp()))
		return msg_new, 200

	@marshal_with(resource_fields)
	def post(self, chat_id, msg_time):
		args = parser.parse_args()
		db.session.add(Message(author=g.user.username, contents=args['msg'],
			chatroom=chat_id))
		db.session.commit()
		
		msg_new = Message.query.filter_by(contents=args['msg']).first()
		msg_new = MsgDao(msg_id=msg_new.msg_id, contents=msg_new.contents,
					author=msg_new.author, time=msg_new.time.timestamp())
		return msg_new, 201


class DeleteForm(FlaskForm):
	delete = HiddenField('delete')
	cancel = SubmitField('Delete')


@app.cli.command('initdb')
def initdb_command():
	db.create_all()
	print('Initialized the database.')


@app.before_request
def before_request():
	g.user = None
	if 'id' in session:
		g.user = User.query.filter_by(user_id=session['id']).first()

@app.route('/')
def default():
	if not g.user:
		return redirect(url_for('login'))
	return redirect(url_for('dashboard'))


@app.route('/dashboard/', methods=['GET', 'POST'])
def dashboard():
	if not g.user:
		return redirect(url_for('login'))

	rooms = Chatroom.query.all()
	error = None
	delete = DeleteForm(prefix='delete')

	if request.method == 'POST' and delete.validate():
		room = Chatroom.query.filter_by(chat_id=delete.delete.data).first()
		if room is not None:
			Message.query.filter_by(chatroom=room.chat_id).delete(synchronize_session=False)
			Chatroom.query.filter_by(chat_id=room.chat_id).delete(synchronize_session=False)
			db.session.commit()
			rooms = Chatroom.query.all()

	elif request.method == 'POST':
		name = request.form['name']
		if not name:
			error = 'Please name your chatroom.'
		elif Chatroom.query.filter_by(name=name).first():
			error = 'Name already taken!'
		else:
			db.session.add(Chatroom(name=name, owner=g.user.username))
			db.session.commit()
			chatroom = Chatroom.query.filter_by(name=name).first()
			return redirect(url_for('chatroom', chat_id=chatroom.chat_id,
				msg_time=0))

	return render_template('dashboard.html', title='Dashboard',
		error=error, rooms=rooms, user=g.user.username, delete=delete)


@app.route('/login/', methods=['GET', 'POST'])
def login():
	if g.user:
		return redirect(url_for('dashboard'))
	error = None
	if request.method == 'POST':
		user = User.query.filter_by(username=request.form['username']).first()
		if user is None:
			error = 'Invalid username'
		elif not check_password_hash(user.pw_hash, request.form['password']):
			error = 'Invalid password'
		else:
			session['id'] = user.user_id
			flash('Succesfully Logged In!')
			return redirect(url_for('dashboard'))
	return render_template('login.html', title='Login', error=error)


@app.route('/logout/')
def logout():
	session.pop('id', None)
	flash('Logout Successful!')
	return redirect(url_for('login'))


@app.route('/register/', methods=['GET', 'POST'])
def register():
	# if a user is logged in, redirect them to the homepage
	if g.user:
		return redirect(url_for('home'))
	error = None
	if request.method == 'POST':
		if not request.form['username']:
			error = 'You must enter a username.'
		elif not request.form['email']:
			error = 'You must enter a email.'
		elif '@' not in request.form['email']:
			error = 'Not a valid email.'
		elif not request.form['password']:
			error = 'You must enter a password.'
		elif not request.form['password2']:
			error = 'You must repeat your password.'
		elif request.form['password'] != request.form['password2']:
			error = 'The two passwords do not match.'
		elif User.query.filter_by(username=request.form['username']).first() is not None:
			error = 'The username is already taken.'
		elif User.query.filter_by(email=request.form['email']).first() is not None:
			error = 'The Email is already taken.'
		else:
			db.session.add(User(username=request.form['username'],
				email=request.form['email'],
				pw_hash=generate_password_hash(request.form['password'])))
			db.session.commit()
			flash('Succesfully Registered!')
			return redirect(url_for('login'))
	return render_template('register.html', title='Register', error=error)


@app.route('/chatroom/<int:chat_id>/')
def chatroom(chat_id):
	if not g.user:
		return redirect(url_for('login'))
	if Chatroom.query.filter_by(chat_id=chat_id).first() is None:
		flash('Chatroom no longer exists!')
		return redirect(url_for('dashboard'))
	return render_template('chatroom.html', title="Chatroom", chat_id=chat_id)


def get_user_id(username):
	rv = User.query.filter_by(username=username).first()
	return rv.id if rv else None


api.add_resource(Chatrooms, '/chatroom/<int:chat_id>/<string:msg_time>')


if __name__ == "__main__":
	app.run(host="0.0.0.0", debug=True, threaded=True)
