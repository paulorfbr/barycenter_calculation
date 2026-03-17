import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-kpi-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="kpi-card card" role="figure" [attr.aria-label]="label + ': ' + value">
      <div class="kpi-icon-wrap" [style.background-color]="iconBg" aria-hidden="true">
        <span class="material-icons kpi-icon" [style.color]="iconColor">{{ icon }}</span>
      </div>
      <div class="kpi-content">
        <p class="kpi-label">{{ label }}</p>
        <p class="kpi-value">{{ value }}</p>
        @if (trend) {
          <p class="kpi-trend" [class.trend-up]="trendPositive" [class.trend-down]="!trendPositive">
            <span class="material-icons" aria-hidden="true">
              {{ trendPositive ? 'trending_up' : 'trending_down' }}
            </span>
            {{ trend }}
          </p>
        }
      </div>
    </div>
  `,
  styles: [`
    .kpi-card {
      display: flex;
      align-items: center;
      gap: var(--space-4);
      padding: var(--space-5);
      min-height: 100px;
    }

    .kpi-icon-wrap {
      width: 52px;
      height: 52px;
      border-radius: var(--radius-3);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .kpi-icon { font-size: 28px; }

    .kpi-content { flex: 1; min-width: 0; }

    .kpi-label {
      font-size: var(--font-size-label);
      font-weight: 600;
      color: var(--color-neutral-500);
      text-transform: uppercase;
      letter-spacing: 0.04em;
      margin-bottom: var(--space-1);
    }

    .kpi-value {
      font-size: var(--font-size-h1);
      font-weight: 700;
      color: var(--color-neutral-900);
      font-variant-numeric: tabular-nums;
      line-height: 1.1;
    }

    .kpi-trend {
      display: flex;
      align-items: center;
      gap: 3px;
      margin-top: var(--space-1);
      font-size: var(--font-size-small);
      font-weight: 600;

      .material-icons { font-size: 14px; }
    }

    .trend-up   { color: var(--color-success-600); }
    .trend-down { color: var(--color-danger-600); }
  `]
})
export class KpiCardComponent {
  @Input({ required: true }) label!: string;
  @Input({ required: true }) value!: string | number;
  @Input({ required: true }) icon!: string;
  @Input() trend?: string;
  @Input() trendPositive = true;
  @Input() iconBg = 'var(--color-primary-050)';
  @Input() iconColor = 'var(--color-primary-700)';
}