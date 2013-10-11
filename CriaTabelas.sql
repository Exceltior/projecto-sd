CREATE TABLE Utilizadores
(uid			NUMERIC(7)		CONSTRAINT pk_uid_utilizadores			PRIMARY KEY,
 username		VARCHAR(20)		CONSTRAINT nn_username_utilizadores		CHECK(username IS NOT NULL),
 pass			VARCHAR(20)		CONSTRAINT nn_pass_utilizadores			CHECK(pass IS NOT NULL),
 dinheiro		NUMERIC(7)		CONSTRAINT nn_money_utilizadores		CHECK(dinheiro IS NOT NULL)
 data_registo	DATE           DEFAULT sysdate 
 );

CREATE TABLE Ideias
(iid			NUMERIC(7)		CONSTRAINT pk_iid_ideias		PRIMARY KEY,
 descricao		VARCHAR(100)	CONSTRAINT nn_descricao_ideias	CHECK(descricao IS NOT NULL),
 criador_id		NUMERIC(7)		CONSTRAINT nn_criadorid_ideias	CHECK(criador_id IS NOT NULL),
 activa			BOOLEAN			CONSTRAINT nn_activa_ideias		CHECK(activa IS NOT NULL)
 );

CREATE TABLE Topicos
(tid			NUMERIC(7)		CONSTRAINT pk_tid_topicos		PRIMARY KEY,
 nome			VARCHAR(20)		CONSTRAINT nn_nome_topicos		CHECK(nome IS NOT NULL),
 descricao		VARCHAR(50)		CONSTRAINT nn_descricao_topicos	CHECK(descricao IS NOT NULL),
 uid			NUMERIC(7)		CONSTRAINT fk_uid_topicos		CHECK(uid IS NOT NULL) REFERENCES Utilizadores(uid)
 );

CREATE TABLE RelacaoIdeias
(iid1			NUMERIC(7)		CONSTRAINT pk_iid1_relacaoideias		PRIMARY KEY,
 iid2			NUMERIC(7)		CONSTRAINT pk_iid2_relacaoideias		PRIMARY KEY,
 tipo_relacao	NUMERIC(1)		CONSTRAINT nn_tiporelacao_relacaoideias	CHECK(tipo_relacao IS NOT NULL)
);

CREATE TABLE Shares
(iid			NUMERIC(7)		CONSTRAINT pk_iid_shares		PRIMARY KEY,
 uid			NUMERIC(7)		CONSTRAINT pk_uid_shares		PRIMARY KEY,
 num_shares		NUMERIC(7)		CONSTRAINT nn_numshares_shares	CHECK(num_shares IS NOT NULL),
 valor			NUMERIC(10)		CONSTRAINT nn_valor_shares		CHECK(valor IS NOT NULL)
);

CREATE TABLE Transacoes
(uid1			NUMERIC(7)		CONSTRAINT pk_uid1_transacoes	PRIMARY KEY,
 uid2			NUMERIC(7)		CONSTRAINT pk_uid1_transacoes	PRIMARY KEY,
 valor			NUMERIC(10)		CONSTRAINT nn_valor_transaccoes CHECK(valor IS NOT NULL),
 iid			NUMERIC(7)		CONSTRAINT fk_iid_transacoes	CHECK(iid IS NOT NULL) REFERENCES Ideias(iid)
);

CREATE TABLE TopicosIdeias
(iid 			NUMERIC(7)		CONSTRAINT pk_iid_topicosideias	PRIMARY KEY,
 tid 			NUMERIC(7)		CONSTRAINT pk_tid_topicosideias PRIMARY KEY
);