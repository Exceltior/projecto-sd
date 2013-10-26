Os ficheiros de código fornecidos podem ser compilados com o script build.sh em sistemas Unix. De seguida, os scripts run_client.sh, run_server.sh e run_rmi_server.sh permitem executar o cliente, o servidor e o servidor RMI. Se estes scripts forem executados sem parâmetros, então assumem que todos vão ser executados na mesma máquina e assumem certas configurações por defeito. No entanto, os scripts podem receber parâmetros:

run_client.sh (host 1) (porta comandos host 1) (porta notificações host 2) ...  (host N> (porta comandos host N) (porta notificações host N)

Este script permite, portanto, um número variável de hosts, cada um com uma porta de comandos e de notificações especificada.

run_server.sh (porta de comandos) (porta de escuta pings UDP) (porta de envio pings UDP) (hostname do outro host) (porta de notificações) (hostname do servidor RMI) (campo que a existir indica que o servidor deve iniciar como secundário)

Este script permite definir a porta de escuta de comandos, a porta de escuta de pings UDP, a de envio de pings UDP, a de escuta de notificações, o hostname do outro servidor, do servidor RMI e ainda um campo opcional que, a existir (com qualquer valor) indica que este servidor deve iniciar-se como secundário.

run_rmi_server.sh (hostname da BD)

Este script possibilita a escolha do hostname da Base de Dados, assumindo-se que executa na porta 1521 e que existe um utilizador com o username "sd" e password "sd".

Ainda que possam ser executadas por qualquer ordem, a ordem preferencial de arranque das aplicações é:
1. Servidor RMI
2. Servidor Primário
3. Servidor secundário
4. Cliente


As tabelas da base de dados podem ser criadas com os scripts fornecidos: 'criaTabelas.sql' e 'insereDados.sql'.