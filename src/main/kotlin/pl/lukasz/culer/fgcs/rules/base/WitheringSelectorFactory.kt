package pl.lukasz.culer.fgcs.rules.base

import pl.lukasz.culer.fgcs.rules.BottomWitheringSelector
import pl.lukasz.culer.fgcs.rules.SimpleOccurrenceMembershipAssigner

enum class WitheringSelectorFactory : () -> WitheringSelector {
    BOTTOM_WITHERING {
        override fun invoke(): WitheringSelector = BottomWitheringSelector()
    }
}