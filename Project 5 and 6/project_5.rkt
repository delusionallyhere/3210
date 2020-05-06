; project 5
#lang racket

(define (buildbst listOfNumbers)
  (if (null? listOfNumbers)
    '()
    (addToTree listOfNumbers '() )
  )
)

(define (addToTree nums tree)
  (if (null? nums)
    tree
    (addToTree (getChilds nums) (parseTree tree (getRoot nums) ) ) ; recursive tree
  )
)

(define (parseTree tree val)
  (cond
    ( (null? tree) (list val '() '()) ) ; empty node
    ( (< val (getRoot tree)) (list (getRoot tree) (parseTree(getLeftTree tree)val) (getRightTree tree) ) ) ;recursive search left
    ( (> val (getRoot tree)) (list (getRoot tree) (getLeftTree tree) (parseTree(getRightTree tree)val) ) ) ;recursive search right
  )
)

(define (getRoot bst)
  (car bst) ; root node
)

(define (getLeftTree bst) 
  (car (cdr bst) ) ; left node
)

(define (getRightTree bst)
  (car (cdr (cdr bst) ) ) ; right node
)

(define (getChilds bst)
  (cdr bst)				;chitlins
)
