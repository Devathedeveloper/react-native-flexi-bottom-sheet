import UIKit
import React

@objc(RNBottomSheetView)
class RNBottomSheetView: UIView {
    enum KeyboardBehavior: String {
        case extend
        case fillParent
        case interactive
    }

    private let containerView = UIView()
    private let handleView = UIView()
    private let handleIndicator = UIView()
    private let dimOverlay = DimOverlayView()
    private var panGesture: UIPanGestureRecognizer!
    private var keyboardObserver: KeyboardObserver?

    private var keyboardBehavior: KeyboardBehavior = .interactive
    private var enableHandleGesture = true
    private var enableContentGesture = true
    private var enablePanDownToClose = false
    private var enableDynamicSizing = false

    private var snapCalculator = SnapCalculator()
    private var currentIndex: Int = -1
    private var animator: UIViewPropertyAnimator?

    private var contentHeightConstraint: NSLayoutConstraint?
    private var bottomConstraint: NSLayoutConstraint!

    @objc var onChange: RCTBubblingEventBlock?
    @objc var onAnimate: RCTBubblingEventBlock?
    @objc var onOpen: RCTBubblingEventBlock?
    @objc var onClose: RCTBubblingEventBlock?
    @objc var onGestureStart: RCTBubblingEventBlock?
    @objc var onGestureEnd: RCTBubblingEventBlock?

    @objc var snapPoints: [Any] = [] {
        didSet { resolveSnapPoints() }
    }
    @objc var index: NSNumber? {
        didSet { if let value = index?.intValue { moveToIndex(value, animated: true) } }
    }
    @objc var enableContentPanningGesture: NSNumber = 1 {
        didSet { enableContentGesture = enableContentPanningGesture.boolValue }
    }
    @objc var enableHandlePanningGesture: NSNumber = 1 {
        didSet { enableHandleGesture = enableHandlePanningGesture.boolValue; handleView.isHidden = !enableHandleGesture }
    }
    @objc var enablePanDownToClose: NSNumber = 0 {
        didSet { enablePanDownToClose = enablePanDownToClose.boolValue }
    }
    @objc var enableDynamicSizing: NSNumber = 0 {
        didSet { enableDynamicSizing = enableDynamicSizing.boolValue; resolveSnapPoints() }
    }
    @objc var keyboardBehaviorProp: NSString? {
        didSet { keyboardBehavior = KeyboardBehavior(rawValue: keyboardBehaviorProp as String? ?? "interactive") ?? .interactive }
    }
    @objc var backgroundColorProp: UIColor? {
        didSet { containerView.backgroundColor = backgroundColorProp ?? .systemBackground }
    }
    @objc var handleHeight: NSNumber? {
        didSet { if let value = handleHeight?.doubleValue { handleHeightConstraint.constant = CGFloat(value) } }
    }
    @objc var handleIndicatorColor: UIColor? {
        didSet { handleIndicator.backgroundColor = handleIndicatorColor ?? .systemGray }
    }
    @objc var cornerRadius: NSNumber? {
        didSet { containerView.layer.cornerRadius = CGFloat(cornerRadius?.doubleValue ?? 16.0) }
    }

    private lazy var handleHeightConstraint: NSLayoutConstraint = handleView.heightAnchor.constraint(equalToConstant: 24)

    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }

    private func setup() {
        clipsToBounds = false

        dimOverlay.translatesAutoresizingMaskIntoConstraints = false
        addSubview(dimOverlay)
        NSLayoutConstraint.activate([
            dimOverlay.topAnchor.constraint(equalTo: topAnchor),
            dimOverlay.leadingAnchor.constraint(equalTo: leadingAnchor),
            dimOverlay.trailingAnchor.constraint(equalTo: trailingAnchor),
            dimOverlay.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        dimOverlay.alpha = 0
        dimOverlay.addTarget(self, action: #selector(didTapOverlay), for: .touchUpInside)

        containerView.translatesAutoresizingMaskIntoConstraints = false
        containerView.backgroundColor = .systemBackground
        containerView.layer.cornerRadius = 16
        containerView.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
        containerView.layer.masksToBounds = true
        addSubview(containerView)

        handleView.translatesAutoresizingMaskIntoConstraints = false
        containerView.addSubview(handleView)
        handleIndicator.translatesAutoresizingMaskIntoConstraints = false
        handleView.addSubview(handleIndicator)
        handleIndicator.backgroundColor = .systemGray4
        handleIndicator.layer.cornerRadius = 2

        handleHeightConstraint.isActive = true
        NSLayoutConstraint.activate([
            handleView.topAnchor.constraint(equalTo: containerView.topAnchor),
            handleView.leadingAnchor.constraint(equalTo: containerView.leadingAnchor),
            handleView.trailingAnchor.constraint(equalTo: containerView.trailingAnchor),
            handleIndicator.centerXAnchor.constraint(equalTo: handleView.centerXAnchor),
            handleIndicator.centerYAnchor.constraint(equalTo: handleView.centerYAnchor),
            handleIndicator.widthAnchor.constraint(equalToConstant: 48),
            handleIndicator.heightAnchor.constraint(equalToConstant: 4)
        ])

        bottomConstraint = containerView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: bounds.height)
        NSLayoutConstraint.activate([
            containerView.leadingAnchor.constraint(equalTo: leadingAnchor),
            containerView.trailingAnchor.constraint(equalTo: trailingAnchor),
            bottomConstraint,
        ])

        panGesture = UIPanGestureRecognizer(target: self, action: #selector(handlePan(_:)))
        panGesture.delegate = self
        addGestureRecognizer(panGesture)

        keyboardObserver = KeyboardObserver { [weak self] visible, frame in
            self?.handleKeyboard(visible: visible, frame: frame)
        }
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        if snapCalculator.snapPoints.isEmpty { resolveSnapPoints() }
        if currentIndex >= 0 { applyOffset(for: CGFloat(currentIndex)) }
    }

    private func resolveSnapPoints() {
        let height = bounds.height
        guard height > 0 else { return }
        var points = snapPoints
        if enableDynamicSizing {
            let contentHeight = containerView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize).height
            points.append(contentHeight)
        }
        snapCalculator.setSnapPoints(points, containerHeight: height)
        if currentIndex >= 0 {
            let point = snapCalculator.snapPoints[min(currentIndex, snapCalculator.snapPoints.count - 1)]
            animate(to: point)
        }
    }

    private func applyOffset(for index: CGFloat) {
        guard !snapCalculator.snapPoints.isEmpty else { return }
        let point = snapCalculator.snapPoints[Int(index)]
        bottomConstraint.constant = bounds.height - point
        dimOverlay.alpha = min(1, point / bounds.height)
    }

    private func animate(to position: CGFloat) {
        animator?.stopAnimation(true)
        bottomConstraint.constant = bounds.height - position
        animator = UIViewPropertyAnimator(duration: 0.25, dampingRatio: 0.9) {
            self.layoutIfNeeded()
            self.dimOverlay.alpha = min(1, position / max(self.bounds.height, 1))
        }
        animator?.addCompletion { [weak self] _ in
            guard let self = self else { return }
            let index = self.snapCalculator.index(for: position)
            if self.currentIndex != index {
                let wasClosed = self.currentIndex == -1
                self.currentIndex = index
                self.onChange?( ["index": index] )
                if wasClosed && index >= 0 { self.onOpen?([:]) }
                if index < 0 { self.onClose?([:]) }
            }
        }
        animator?.startAnimation()
    }

    private func handleKeyboard(visible: Bool, frame: CGRect) {
        switch keyboardBehavior {
        case .extend:
            if visible { expandToLargerSnap() }
        case .fillParent:
            if visible { expandToMax() }
            else { moveToIndex(currentIndex, animated: true) }
        case .interactive:
            break
        }
    }

    private func expandToLargerSnap() {
        let nextIndex = min(currentIndex + 1, snapCalculator.snapPoints.count - 1)
        moveToIndex(nextIndex, animated: true)
    }

    private func expandToMax() {
        moveToIndex(snapCalculator.snapPoints.count - 1, animated: true)
    }

    private func moveToIndex(_ index: Int, animated: Bool) {
        guard !snapCalculator.snapPoints.isEmpty else { return }
        let clamped = max(-1, min(index, snapCalculator.snapPoints.count - 1))
        currentIndex = clamped
        let target = clamped >= 0 ? snapCalculator.snapPoints[clamped] : 0
        if animated {
            animate(to: target)
        } else {
            bottomConstraint.constant = bounds.height - target
            setNeedsLayout()
        }
    }

    @objc private func didTapOverlay() {
        guard enablePanDownToClose else { return }
        moveToIndex(-1, animated: true)
        onClose?([:])
    }

    @objc private func handlePan(_ gesture: UIPanGestureRecognizer) {
        let translation = gesture.translation(in: self).y
        let velocity = gesture.velocity(in: self).y
        let height = bounds.height
        switch gesture.state {
        case .began:
            animator?.stopAnimation(true)
            onGestureStart?([:])
        case .changed:
            let newConstant = bottomConstraint.constant + translation
            bottomConstraint.constant = min(max(newConstant, 0), height)
            layoutIfNeeded()
            gesture.setTranslation(.zero, in: self)
            let progress = height - bottomConstraint.constant
            onAnimate?( ["index": currentIndex, "position": progress] )
        case .ended, .cancelled:
            let progress = height - bottomConstraint.constant
            let target = snapCalculator.closestSnap(to: progress, velocity: velocity)
            animate(to: target)
            onGestureEnd?([:])
        default:
            break
        }
    }

    deinit { keyboardObserver?.dispose() }
}

extension RNBottomSheetView: UIGestureRecognizerDelegate {
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        if gestureRecognizer == panGesture {
            if touch.view?.isDescendant(of: handleView) == true { return enableHandleGesture }
            if touch.view?.isDescendant(of: containerView) == true { return enableContentGesture }
        }
        return true
    }
}
