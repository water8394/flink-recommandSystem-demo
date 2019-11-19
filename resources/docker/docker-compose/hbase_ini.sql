create 'con',{NAME => 'log', VERSIONS => 2}

create 'prod',{NAME => 'sex', VERSIONS => 2},{NAME => 'age', VERSIONS => 2}

create 'user',{NAME => 'country', VERSIONS => 2},{NAME => 'color', VERSIONS => 2},{NAME => 'style', VERSIONS => 2}

create 'u_history',{NAME => 'p', VERSIONS => 2}

create 'p_history',{NAME => 'p', VERSIONS => 2}

create 'ps',{NAME => 'p', VERSIONS => 2}

create 'px',{NAME => 'p', VERSIONS => 2}

create 'u_interest',{NAME => 'p', VERSIONS => 2}

put 'user','1','color:red','10'
