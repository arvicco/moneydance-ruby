# The transactions from Auto->Fuel will be moved to Auto->Service
# Change the account names as necessary
orig_account_name = "Fuel"
dest_account_name = "Auto:Service"

# Extending Moneydance Account class with methods collecting
# all its descendants (both direct and indirect), and finding
# account with a specific name (possible partial) among them
class Java::ComMoneydanceAppsMdModel::Account
  def descendants
    [self, self.sub_accounts.map { |a| a.descendants }].flatten
  end

  def find name
    re = Regexp.new(name)
    self.descendants.find { |a| a.account_name =~ re || a.full_account_name =~ re }
  end
end

# This finds account even by partial name, such as "Fuel" instead of full "Auto:Fuel"
orig_account = ROOT.find orig_account_name
dest_account = ROOT.find dest_account_name

if orig_account && dest_account

  txns = TRANS.get_transactions_for_account(orig_account).all_txns.to_a

  puts "Moving #{txns.size} transactions from #{orig_account} to #{dest_account}..."
  txns.each { |t| puts t; t.set_account(dest_account) }

  ROOT.refresh_account_balances
end
