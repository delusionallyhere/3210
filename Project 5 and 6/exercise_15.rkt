#lang racket

(define (store value var table)
  (if (null? table)
      (list (list var value))
      (if (equal? var (first (first table)))
          (cons (list var value) (rest table))
          (cons (first table) (store value var (rest table))))))

(define (retrieve var table)
  (if (equal? var (first (first table)))
      (second (first table))
      (retrieve var (rest table))
      )
  )

(define (assign x y table)
  (if (number? y)
      (store y x table)
      (store (retrieve y table) x table)
      )
  )