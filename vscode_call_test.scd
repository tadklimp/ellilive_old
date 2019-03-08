
SuperDirt.start

ElliLive.new(3,[\sample, \sample, \sample])

"source ~/.zshrc; cd /Users/Makis/Documents/Tests/ ; code -g test.tidal:54 ".systemCmd;
"source ~/.zshrc; cd /Users/Makis/Documents/Tests/ ; sed -n '/pat3/=' test.tidal ".unixCmdGetStdOut;

"echo $PATH".unix
"source ~/.zshrc; echo $PATH".unixCmd
EE.voices[0].type
{SinOsc.ar(mul:0.4)}.play

~eval = " \" :$(sed -n '/pat3/=' test.tidal) \" Enter c-e Enter Escape";
~bu4 = "\":3b bufdo put=test\\<CR>\" ";
~bu4 = "\":silent exec \"b4\" | exec \"/pat1\" | exec \"c-e\" ";
~bu2 = "\" :b2 \" Enter \"/pat2\" Enter ";
~bu1 = "\" :b1 \" Enter \"/pat2\" Enter ";
~run = "c-e Enter "
~path = "source ~/.zshrc; cd /Users/Makis/Documents/Tidal/Ellicist/live0/ ; tmux send-keys -t tidal.0 Escape "



("sleep 1 ; "++ ~path++~bu4).unixCmd;

// send --patNum to Vim
(~path++("\"{) O--pat9 \" Escape  "))
.unixCmd



// try :vert sb N

// TODO
- VIM
x how to save buffers with project
x set "patX" above the current block code when saved in a pad
- can vim report how many buffers it holds?
x automatically renumber buffers in vim when closing one = not possible
x when calling buffer jump to its window = ok, but not sure i want that
-

-PLAYbutton should trigger scene OR sent hush
-Voicebutton should also trigger b:w