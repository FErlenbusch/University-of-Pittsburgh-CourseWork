import os

from flask import Flask, render_template, request, url_for, redirect
from flask import session, g

from werkzeug import check_password_hash, generate_password_hash
from datetime import date, timedelta

from flask_wtf import FlaskForm
from wtforms import SubmitField, HiddenField
from wtforms.fields.html5 import DateField
from wtforms_components import DateRange


from models import db, User, Event, Job

app = Flask(__name__)

# configuration
DEBUG = True
SECRET_KEY = 'development key'

SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(app.root_path,
	'catering.db')
SQLALCHEMY_TRACK_MODIFICATIONS = False

app.config.from_object(__name__)
app.config.from_envvar('CATERERS_SETTINGS', silent=True)

db.init_app(app)

class ScheduleForm(FlaskForm):
	date = DateField('date', validators=[DateRange(min=date.today() +
		timedelta(days=1))])
	submit = SubmitField('Submit')

class CancelForm(FlaskForm):
	date = HiddenField('date')
	cancel = SubmitField('Cancel')

@app.cli.command('initdb')
def initdb_command():
	db.create_all()
	db.session.add(User(username='owner', user_type='1', first='Fred',
		last='Erlenbusch', pw_hash=generate_password_hash('pass')))
	db.session.commit()
	print('Initialized the database.')

def get_user_id(username):
	rv = User.query.filter_by(username=username).first()
	return rv.id if rv else None

def get_event_id(event_date):
	rv = Event.query.filter_by(event_date=event_date).first()
	return rv.event_date if rv else None

@app.before_request
def before_request():
	g.user = None
	if 'id' in session:
		g.user = User.query.filter_by(id=session['id']).first()

@app.route('/')
def default():
	if not g.user:
		return redirect(url_for('login'))
	return redirect(url_for('dashboard', id=session['id']))

@app.route('/login/', methods=['GET', 'POST'])
def login():
	if g.user:
		return redirect(url_for('dashboard', id=session['id']))
	error = None
	if request.method == 'POST':
		user = User.query.filter_by(username=request.form['user']).first()
		if user is None:
			error = 'Invalid username'
		elif not check_password_hash(user.pw_hash, request.form['password']):
			error = 'Invalid password'
		else:
			session['id'] = user.id
			return redirect(url_for('dashboard', id=session['id']))
	return render_template('login.html', title='Login', error=error)

@app.route('/logout/')
def logout():
	if not g.user:
		return redirect(url_for('login'))
	session.pop('id', None)
	return render_template('logoutPage.html', title='Logout')

@app.route('/dashboard/', methods=['GET', 'POST'])
def dashboard():
	if not g.user:
		return redirect(url_for('login'))
	if g.user.user_type == '3':
		return redirect(url_for('client_dashboard'))
	error = None
	if request.method == 'POST':
		job = Job.query.filter_by(job_event=request.form['event'],
			job_worker=g.user.id).first()
		if job is None:
			db.session.add(Job(job_event=request.form['event'],
				job_worker=g.user.id))
			db.session.commit()
			error = 'You have sucessfuly signed up for event.'
	events = Event.query.order_by(Event.event_date).all()
	users = User.query.all()
	return render_template('dashboard.html', level=g.user.user_type,
		events=events, users=users, error=error, id=session['id'], title='Dashboard')

@app.route('/client_dashboard/', methods=['GET', 'POST'])
def client_dashboard():
	if not g.user:
		return redirect(url_for('login'))
	error = None
	alert = None
	sched = ScheduleForm(prefix='sched')
	cancl = CancelForm(prefix='cancl')
	if request.method == 'POST' and sched.validate():
		if get_event_id(sched.date.data) is not None:
			error = 'Error: The date you selected is already booked.'
		else:
			db.session.add(Event(client_id=g.user.id, event_date=sched.date.data))
			db.session.commit()
			error = 'Your event has been Scheduled.'
	elif request.method == 'POST' and not cancl.validate() and not sched.validate():
		error = 'Error: The date you selected is in the past.'
	if request.method == 'POST' and cancl.validate():
		print(cancl.date.data)
		event = Event.query.filter_by(event_id=cancl.date.data).first()
		if event is not None:
			Job.query.filter_by(job_event=event.event_date).delete(synchronize_session=False)
			Event.query.filter_by(event_id=event.event_id).delete(synchronize_session=False)
			db.session.commit()
			alert = 'You have sucessfuly canceled your event.'
	events = Event.query.order_by(Event.event_date).all()
	return render_template('client_dashboard.html', events=events, cancl=cancl,
		sched=sched, id=session['id'], level=g.user.user_type, error=error,
		alert=alert, title='Dashboard')

@app.route('/register_client/', methods=['GET', 'POST'])
def register_client():
	if g.user:
		return redirect(url_for('dashboard', id=session['id']))
	error = None
	if request.method == 'POST':
		if not request.form['username']:
			error = 'You have to enter a username.'
		elif not request.form['first']:
			error = 'You have to enter a first name.'
		elif not request.form['last']:
			error = 'You have to enter a last name.'
		elif not request.form['password']:
			error = 'You have to enter a password.'
		elif request.form['password'] != request.form['password2']:
			error = 'The two passwords do not match.'
		elif get_user_id(request.form['username']) is not None:
			error = 'The username is already taken.'
		else:
			db.session.add(User(username=request.form['username'], user_type='3',
				first=request.form['first'], last=request.form['last'],
				pw_hash=generate_password_hash(request.form['password'])))
			db.session.commit()
			return redirect(url_for('registered_success'))
	return render_template('register_client.html',
		title='Register Client', error=error)

@app.route('/register_employee/', methods=['GET', 'POST'])
def register_employee():
	if session['id'] != 1:
		return redirect(url_for('dashboard', id=session['id']))
	error = None
	if request.method == 'POST':
		if not request.form['username']:
			error = 'You have to enter a username.'
		elif not request.form['first']:
			error = 'You have to enter a first name.'
		elif not request.form['last']:
			error = 'You have to enter a last name.'
		elif not request.form['password']:
			error = 'You have to enter a password.'
		elif request.form['password'] != request.form['password2']:
			error = 'The two passwords do not match.'
		elif get_user_id(request.form['username']) is not None:
			error = 'The username is already taken.'
		else:
			db.session.add(User(username=request.form['username'], user_type='2',
				first=request.form['first'], last=request.form['last'],
				pw_hash=generate_password_hash(request.form['password'])))
			db.session.commit()
			return redirect(url_for('registered_success'))
	return render_template('register_employee.html', level=g.user.user_type,
		id=session['id'], title='Register Employee', error=error)

@app.route('/registered_success/')
def registered_success():
	if not g.user:
		return redirect(url_for('login'))
	return render_template('registered_success.html', level=g.user.user_type,
		title='Registered Success')
