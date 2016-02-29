curl -L 192.168.99.100/newuser.php --data "USERNAME=switch202&PASSWORD=password&PASSWORD2=password&FNAME=Grumpy&LNAME=OldFart&EMAIL=scnolton@oakland.edu";
curl -L 192.168.99.100/newacqu.php --data "USERNAME=loopy&FNAME=Grumpy&LNAME=OldFart";
curl -L 192.168.99.100/relate.php --data "USERNAME=switch202&ACQUNAME=loopy&RELATION=brother&MESSAGE=my older brother. he is so cool.";