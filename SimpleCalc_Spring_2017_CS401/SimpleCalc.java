import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.util.*;

public class SimpleCalc
{
	JFrame window;  
	Container content ;
	JButton[] digits = new JButton[12]; 
	JButton[] ops = new JButton[4];
	JTextField expression;
	JTextField testBox;
	JButton equals;
	JTextField result;
	
	public SimpleCalc()
	{
		window = new JFrame( "Simple Calc");
		content = window.getContentPane();
		content.setLayout(new GridLayout(2,1)); 
		ButtonListener listener = new ButtonListener();
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1,3));
		
		expression = new JTextField();
		expression.setFont(new Font("verdana", Font.BOLD, 16));
		expression.setText("");
		expression.addActionListener( listener );
		
		equals = new JButton("=");
		equals.setFont(new Font("verdana", Font.BOLD, 20 ));
		equals.addActionListener( listener ); 
		
		result = new JTextField();
		result.setFont(new Font("verdana", Font.BOLD, 16));
		result.setText("");
		
		topPanel.add(expression);
		topPanel.add(equals);
		topPanel.add(result);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1,2)); 
	
		JPanel  digitsPanel = new JPanel();
		digitsPanel.setLayout(new GridLayout(4,3));	
		
		for (int i=0 ; i<10 ; i++ )
		{
			digits[i] = new JButton( ""+i );
			digitsPanel.add( digits[i] );
			digits[i].addActionListener( listener ); 
		}
		digits[10] = new JButton( "C" );
		digitsPanel.add( digits[10] );
		digits[10].addActionListener( listener ); 

		digits[11] = new JButton( "CE" );
		digitsPanel.add( digits[11] );
		digits[11].addActionListener( listener ); 		
	
		JPanel opsPanel = new JPanel();
		opsPanel.setLayout(new GridLayout(4,1));
		String[] opCodes = { "+", "-", "*", "/" };
		for (int i=0 ; i<4 ; i++ )
		{
			ops[i] = new JButton( opCodes[i] );
			opsPanel.add( ops[i] );
			ops[i].addActionListener( listener ); 
		}
		bottomPanel.add( digitsPanel );
		bottomPanel.add( opsPanel );
		
		content.add( topPanel );
		content.add( bottomPanel );
	
		window.setSize( 640,480);
		window.setVisible( true );
	}

	class ButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			Component whichButton = (Component) e.getSource();
			
			for (int i=0 ; i<10 ; i++ )
				if (whichButton == digits[i])
				{
					expression.setText( expression.getText() + i );
				}
			for (int i=0 ; i<4 ; i++) 
			{
				Boolean check = true;
				for (int j=0 ; j < 4 ; j++)
					if (expression.getText().endsWith( ops[j].getText() ))
						check = false;
				if (expression.getText().length() == 0)
					check = false;
				if (whichButton == ops[i])
					if (check)
						expression.setText( expression.getText() + ops[i].getText() );
			}
			if (whichButton == digits[10])
				expression.setText( "" );

			if (whichButton == digits[11]) 
			{
				expression.setText( new Tokenizer( expression.getText()).removeLastExpr() );
			}
			if (whichButton == equals || whichButton == expression)
				result.setText( new Tokenizer( expression.getText()).evaluate() );
		}
		
	}

	public class Tokenizer
	{
		private ArrayList<String> operatorList = new ArrayList<String>();
		private ArrayList<String> operandList = new ArrayList<String>();

		public Tokenizer()
		{
			this( "" );
		}
		public Tokenizer( String expr )
		{
			addTokens( expr );
		}
		
		public void addTokens( String expr )
		{
			StringTokenizer st = new StringTokenizer( expr,"+-*/", true );
			while ( st.hasMoreTokens() )
			{
				String token = st.nextToken();
				if ( "+-/*".contains( token ) )
					operatorList.add( token );
				else
					operandList.add( token );
    		}
		}
		public String removeLastExpr()
		{
			if ( operatorSize()>=operandSize() && operatorSize()!=0 )
				operatorList.remove( operatorSize()-1 );
			else
			{
				if ( operatorSize() >= 1 )
				{
					operatorList.remove( operatorSize()-1 );
					operandList.remove( operandSize()-1 );
				}
				else if ( operandSize()>=1 && operatorSize()==0 )
					operandList.remove( operandSize()-1 );
			}
			return getExpr();
		}
		public String evaluate()
		{
			ArrayList<String> operatorListEval =  operatorList;
			ArrayList<String> operandListEval = operandList;
			double[] operands = new double[ operandListEval.size() ];
			setDoubleArr( operatorListEval, operandListEval, operands );
			double solution = 0;

			if ( operatorListEval.size() >= operandListEval.size() )
				return "ERROR: Invalid Expression!" ;
			else if ( operatorListEval.size() > 0 ) 
			{
				while ( operatorListEval.contains( "*".toString() ) || operatorListEval.contains( "/".toString() ))
				{
					for (int i = 0; i<operatorListEval.size(); i++)
					{
						if ( "*".equals( operatorListEval.get(i).toString() ) )
						{
							solution = operands[i] * operands[i+1];
							i = modArrs( operatorListEval, operandListEval, operands, solution, i ); 
						}
						else if ( "/".equals( operatorListEval.get(i).toString() ) )
						{
							if( operands[i+1] == 0 )
							{
								return "ERROR: Dividing by 0 is Invalid";
							}
							else
							{
								solution = operands[i] / operands[i+1];
								i = modArrs( operatorListEval, operandListEval, operands, solution, i );
							}
						}
					}
				}
				while ( operatorListEval.contains( "+".toString() ) || operatorListEval.contains( "-".toString() ))
				{
					for (int i = 0; i < operatorListEval.size(); i++)
					{
						if ( "+".equals( operatorListEval.get(i).toString() ) )
						{
							solution = operands[i] + operands[i+1];
							i = modArrs( operatorListEval, operandListEval, operands, solution, i );
						}
						else if ( "-".equals( operatorListEval.get(i).toString() ) )
						{
							solution = operands[i] - operands[i+1];
							i = modArrs( operatorListEval, operandListEval, operands, solution, i );
						}
					}
				}
			}
			return Double.toString(solution);
		}

		public int operatorSize()
		{
			return operatorList.size();
		}
		public int operandSize()
		{
			return operandList.size();
		}
		public String getExpr()
		{
			String expr = new String("");
			if ( operandSize() == operatorSize() )
				for ( int i=0 ; i < operandSize() ; i++)
						expr = expr + operandList.get(i) + operatorList.get(i);
			if ( operandSize() > operatorSize() )
				for ( int i=0 ; i < operandSize() ; i++ ) 
				{
					if (i == 0)
						expr = operandList.get(i);
					else
						expr = expr + operatorList.get(i-1) + operandList.get(i);
				}			return expr;
		}

		private void setDoubleArr( ArrayList<String> operatorListEval, ArrayList<String> operandListEval, double[] operands )
		{
			for (int i =0; i<operandListEval.size(); i++)
					operands[i] = Double.parseDouble( operandListEval.get(i) );
		}
		private int  modArrs( ArrayList<String> operatorListEval, ArrayList<String> operandListEval, double[] operands, 
			double solution, int i )
		{
			operandListEval.set(i, Double.toString(solution));
			operatorListEval.remove(i);
			operandListEval.remove(i+1);
			setDoubleArr( operatorListEval, operandListEval, operands );
			return i;
		}
	}

	public static void main(String [] args)
	{
		new SimpleCalc();
	}
}

