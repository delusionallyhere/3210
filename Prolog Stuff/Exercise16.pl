locate(X, [X|_], 0) :- !.
locate(X, [_|L], Index):- locate(X, L, Index1), !, Index is Index1+1.

mix([X|Xs], [Y|Ys], [X,Y|Zs]) :- !, mix(Xs, Ys, Zs).
mix([], Ys, Ys) :- !.
mix(Xs, [], Xs).
