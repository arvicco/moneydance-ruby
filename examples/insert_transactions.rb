# Script that loads a list of transactions into a Moneydance account

import com.moneydance.apps.md.model.AbstractTxn
import com.moneydance.apps.md.model.ParentTxn
import com.moneydance.apps.md.model.SplitTxn

CLEARED = AbstractTxn::STATUS_CLEARED # ::STATUS_RECONCILING, ::STATUS_UNRECONCILED

#        Date       Description/Payee     Acct   Amount  Other currency amount
LIST = [[20110525, 'Test transaction 1', 'Test2', -20.00, 10],
        [20110525, 'Test transaction 2', 'Test2', 24.00, -12.0],
        [20110525, 'Test transaction 3', 'Test2', 26.00],
        [20110526, 'Test transaction 4', 'Test1', 27],
        [20110527, 'Test transaction 5', 'Test1', -28.01],
        [20110528, 'Test transaction 6', 'Test1', 29.50]]

ACCOUNT = 'Test'

def account_with_currency acct_name
  acct = ROOT.get_account_by_name acct_name
  raise "Account #{acct_name} is invalid" unless acct
  [acct, acct.currency_type]
end

def insert_into acct_name, list

  acct, currency = account_with_currency acct_name

  list.each do |entry|
    date, desc, other_acct_name, amount, other_amount = *entry

    other_acct, other_currency = account_with_currency other_acct_name

    adjusted_amount = -amount * 10 ** currency.decimal_places
    rate = other_currency.raw_rate / currency.raw_rate
    other_amount =
        if other_amount
          other_amount * 10 ** other_currency.decimal_places
        else
          adjusted_amount * rate
        end

    t = ParentTxn.new date, date, date, date.to_s, acct, desc, 'Memmo', -1, CLEARED
    s = SplitTxn.new t, adjusted_amount, other_amount, rate, other_acct, desc, -1, CLEARED

    t.add_split s
    TRANS.add_new_txn t

    puts t
  end
end

insert_into ACCOUNT, LIST

p 'Finished!'

