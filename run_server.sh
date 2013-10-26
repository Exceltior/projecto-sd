#!/bin/bash
java -cp "./ojdbc6.jar:." -Djava.security.manager -Djava.security.policy=security.policy Server $@
