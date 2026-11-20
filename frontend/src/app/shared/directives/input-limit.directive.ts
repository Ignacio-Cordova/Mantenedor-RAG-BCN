import { Directive, ElementRef, HostListener, OnInit, Renderer2 } from '@angular/core';

@Directive({
  selector: 'input[maxlength], textarea[maxlength]',
  standalone: false
})
export class InputLimitDirective implements OnInit {
  private readonly element: HTMLInputElement | HTMLTextAreaElement;
  private readonly messageElement: HTMLSpanElement;
  private maxLength = 0;

  constructor(
    elementRef: ElementRef<HTMLInputElement | HTMLTextAreaElement>,
    private renderer: Renderer2
  ) {
    this.element = elementRef.nativeElement;
    this.messageElement = renderer.createElement('span');
    renderer.addClass(this.messageElement, 'input-limit-message');
    renderer.setAttribute(this.messageElement, 'aria-live', 'polite');
    renderer.setStyle(this.messageElement, 'display', 'none');
  }

  ngOnInit(): void {
    this.maxLength = Number(this.element.getAttribute('maxlength'));
    if (!this.maxLength || !this.element.parentElement) {
      return;
    }

    this.renderer.appendChild(
      this.element.parentElement,
      this.messageElement
    );
    this.validateCurrentValue();
  }

  @HostListener('beforeinput', ['$event'])
  onBeforeInput(event: InputEvent): void {
    if (!event.data || event.inputType.startsWith('delete')) {
      return;
    }

    const selectionLength = this.getSelectionLength();
    const nextLength = this.element.value.length - selectionLength + event.data.length;
    if (nextLength > this.maxLength) {
      event.preventDefault();
      this.showMessage();
    }
  }

  @HostListener('paste', ['$event'])
  onPaste(event: ClipboardEvent): void {
    const pastedText = event.clipboardData?.getData('text') ?? '';
    const nextLength = this.element.value.length - this.getSelectionLength() + pastedText.length;
    if (nextLength > this.maxLength) {
      event.preventDefault();
      this.showMessage();
    }
  }

  @HostListener('input')
  onInput(): void {
    this.validateCurrentValue();
  }

  private getSelectionLength(): number {
    const start = this.element.selectionStart ?? this.element.value.length;
    const end = this.element.selectionEnd ?? start;
    return end - start;
  }

  private validateCurrentValue(): void {
    if (this.element.value.length > this.maxLength) {
      this.showMessage();
    } else {
      this.hideMessage();
    }
  }

  private showMessage(): void {
    this.renderer.setProperty(
      this.messageElement,
      'textContent',
      `Máximo ${this.maxLength} caracteres.`
    );
    this.renderer.removeStyle(this.messageElement, 'display');
    this.renderer.addClass(this.element, 'input-limit-error');
  }

  private hideMessage(): void {
    this.renderer.setStyle(this.messageElement, 'display', 'none');
    this.renderer.removeClass(this.element, 'input-limit-error');
  }
}
