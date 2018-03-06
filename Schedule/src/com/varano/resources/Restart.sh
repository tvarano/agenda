#!/bin/sh

#  Restart.sh
#  
#
#  Created by Thomas Varano on 2/26/18.
#


# $1 = directory
# $2 = file name (including .app)
echo 'restarting agenda'
osascript -e 'quit app "Agenda"'
cd ~/$1
open 'Agenda.app'
