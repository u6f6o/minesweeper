(ns minesweeper.game
  (:use [minesweeper board dispatch ui]))



;;   SCENARIOS:

;;   1. USER CLICKS ON CELL WITH NO MINE
;;      <- ui:    fire :explore-field [1 1]
;;      -> game:  receive :explore-field [1 1]
;;         board: assoc :explored flag to field
;;         goard: check win/lost condition => nothing
;;      <- game:  fire :field-explored [1 1]
;;      -> ui:    receive :field-explored [1 1]
;;         ui:    change icon of field
;;         ui:    repaint cell

;;   2. USER TRIGGERS FINAL STATE
;;      <- ui:    fire :explored-field [1 1]
;;      -> game:  receive: :explore-field [1 1]
;;         board: assoc :explored flag to field
;;         board: check win/lost condition => won/lost
;;      <- game:  fire :game-lost or :game-won
;;      -> ui:    recieve :game-lost or :game-won
;;         ui:    make everything unclickable
;;         ui:    uncover and repaint all cells
;;         ui:    repaint smiley

;;   3. USER SETS FLAG
;;      <- ui:    fire :handle-flag [1 1]
;;      -> game:  receive: :handle-flag [1 1]
;;         board: change flag set on field (true/false)
;;      <- game:  fire :flag-set or :flag-removed
;;      -> ui:    recieve :flag-set or :flag-removed
;;         ui:    change icon of field
;;         ui:    repaint smiley






