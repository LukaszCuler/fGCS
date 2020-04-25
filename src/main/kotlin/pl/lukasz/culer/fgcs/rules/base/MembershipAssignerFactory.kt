package pl.lukasz.culer.fgcs.rules.base

import pl.lukasz.culer.fgcs.rules.SimpleOccurrenceMembershipAssigner

enum class MembershipAssignerFactory : () -> MembershipAssigner {
    SIMPLE_OCCURRENCE {
        override fun invoke(): MembershipAssigner = SimpleOccurrenceMembershipAssigner()
    }
}