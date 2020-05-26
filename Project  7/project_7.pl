rowBelow([A,B], [X]):- X is B-A.
rowBelow([A,B|T], [X|Y]):-  X is B-A,  rowBelow([B|T], Y).

myLast([A], A).
myLast([A|T], X) :- myLast(T, X).

nextItem([A], A).
nextItem(B, N):- myLast(B, X), rowBelow(B, Y), nextItem(Y, Z), N is X + Z.
