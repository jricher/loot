package net.gumbercules.loot.account;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

import net.gumbercules.loot.R;
import net.gumbercules.loot.backend.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AccountAdapter extends ArrayAdapter<Account>
{
	private ArrayList<Account> mAccountList;
	private int mRowResId;
	private Context mContext;
	private LayoutInflater mInflater;

	public AccountAdapter(Context con, int row, ArrayList<Account> acl)
	{
		super(con, 0);
		mAccountList = acl;
		mRowResId = row;
		mContext = con;
		
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setResource(int resource)
	{
		mRowResId = resource;
	}
	
	@Override
	public int getCount()
	{
		return mAccountList.size();
	}

	@Override
	public Account getItem(int position)
	{
		return mAccountList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return mAccountList.get(position).id();
	}

	@Override
	public void add(Account object)
	{
		mAccountList.add(object);
	}

	@Override
	public void insert(Account object, int index)
	{
		mAccountList.add(index, object);
	}

	public void remove(int index)
	{
		mAccountList.remove(index);
	}
	
	public void setPrimary(int index, boolean set)
	{
		for (int i = 0; i < mAccountList.size(); ++i)
		{
			if (i == index)
			{
				mAccountList.get(i).primary = set;
			}
			else
			{
				mAccountList.get(i).primary = false;
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Account acct = mAccountList.get(position);
		View v = createViewFromResource(convertView, parent, mRowResId);

		// find and retrieve the widgets
		TextView AccountName = (TextView)v.findViewById(R.id.AccountName);
		TextView AccountBal = (TextView)v.findViewById(R.id.AccountBalance);
		View star = (View)v.findViewById(R.id.star_image);
		
		if (AccountName != null)
		{
			AccountName.setText(acct.name);
		}
		if (AccountBal != null)
		{
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			String new_currency = Database.getOptionString("override_locale");
			if (new_currency != null && !new_currency.equals(""))
			{
				nf.setCurrency(Currency.getInstance(new_currency));
			}
			Double bal = acct.getActualBalance();
			String text;
			if (bal != null)
			{
				text = nf.format(bal);
			}
			else
			{
				text = "Error Calculating Balance";
			}
			AccountBal.setText(text);
			
			if (bal < 0.0)
			{
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
				if (prefs.getBoolean("color_balance", false))
					AccountBal.setTextColor(Color.rgb(255, 50, 50));
			}
			else
			{
				AccountBal.setTextColor(Color.LTGRAY);
			}
		}
		
		int visibility = View.GONE;
		if (acct.isPrimary())
		{
			visibility = View.VISIBLE;
		}
		star.setVisibility(visibility);

		return v;
	}

	private View createViewFromResource(View convertView, ViewGroup parent, int resource)
	{
		View view;
		
		if (convertView == null)
			view = mInflater.inflate(resource, parent, false);
		else
			view = convertView;
	
		return view;
	}
}