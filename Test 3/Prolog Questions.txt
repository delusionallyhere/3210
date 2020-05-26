%Question 5
seq(0, 1).
seq(1, 2).
seq(N, X):- N > 1, A is N-1, B is N-2,
    seq(A, C), seq(B, D), X is (3*C-D).

%Question 6
genSquares(1,[1]).
genSquares(N, [A,B|X]):- N > 0, C is N * N, D is N - 1, A == C, genSquares(D, [B|X]).
