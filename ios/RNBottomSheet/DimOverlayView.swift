import UIKit

final class DimOverlayView: UIControl {
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }

    private func setup() {
        backgroundColor = UIColor.black.withAlphaComponent(0.4)
        isAccessibilityElement = true
        accessibilityLabel = NSLocalizedString("Bottom sheet dismiss overlay", comment: "")
    }
}
