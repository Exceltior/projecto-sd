#!/bin/bash
PWD_OLD=`pwd`
cd webapp/IdeaBroker/src/main/resources
javac -cp "./../../../../../ojdbc6.jar:." RMI_Server.java
cp RMI_Server.class $PWD_OLD
cd $PWD_OLD
