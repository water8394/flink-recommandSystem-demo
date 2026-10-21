# HBase shell (JRuby), not SQL. Safe to run more than once.
# Same tables as resources/sql/hbase建表语句.

def ensure_table(name, *families)
  if exists(name)
    puts "table #{name} already exists"
  else
    create(name, *families)
    puts "created #{name}"
  end
end

ensure_table 'con', {NAME => 'log', VERSIONS => 2}
ensure_table 'prod', {NAME => 'sex', VERSIONS => 2}, {NAME => 'age', VERSIONS => 2}
ensure_table 'user', {NAME => 'country', VERSIONS => 2}, {NAME => 'color', VERSIONS => 2}, {NAME => 'style', VERSIONS => 2}
ensure_table 'u_history', {NAME => 'p', VERSIONS => 2}
ensure_table 'p_history', {NAME => 'p', VERSIONS => 2}
ensure_table 'ps', {NAME => 'p', VERSIONS => 2}
ensure_table 'px', {NAME => 'p', VERSIONS => 2}
ensure_table 'u_interest', {NAME => 'p', VERSIONS => 2}

put 'user', '1', 'color:red', '10'
