
SuperDirt.start

ElliLive.new(3,[\sample, \sample, \sample])

"source ~/.zshrc; cd /Users/Makis/Documents/Tests/ ; code -g test.tidal:54 ".systemCmd;
"source ~/.zshrc; cd /Users/Makis/Documents/Tests/ ; sed -n '/pat3/=' test.tidal ".unixCmdGetStdOut;

"echo $PATH".unix
"source ~/.zshrc; echo $PATH".unixCmd
EE.voices[0].type
{SinOsc.ar(mul:0.4)}.play

~eval = " \" :$(sed -n '/pat3/=' test.tidal) \" Enter c-e Enter Escape";
~bu4 = "\" :b4 \" Enter \"/d4\" Enter ";
~bu2 = "\" :b2 \" Enter \"/buf3\" Enter ";
~bu1 = "\" :b1 \" Enter \"/pat3\" Enter ";
~run = "c-e Enter "
~path = "source ~/.zshrc; cd /Users/Makis/Documents/Tests/ ; tmux send-keys -t tidal.0 Escape "


(~path++~bu1++~run++~bu2++~run).unixCmd;

// send --patNum to Vim
(~path++("\"{) O--pat9 \" Escape \":w\" Enter ")).unixCmd


// TODO
- VIM
- how to save buffers with project
x set "patX" above the current block code when saved in a pad
