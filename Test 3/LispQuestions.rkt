;question #2
#lang racket

(define (biggest list)
    (first list)
    (if ( < (first list) (biggest (rest list)))
        (biggest (rest list))
        (first list)
    )
)

;question #3
(define (get2d row col array)
    (first (first array))
    (get2d row (- col 1) (cons (rest (first array))(rest array)))
)

;question #4
(define (nextrow row)
  (do ((list row (rest list))
       (result '(1)
         (cons (+ (first list)(second list)) result)))
    ((< (length list) 2) (cons 1 result))))
