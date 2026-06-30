import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Course, CourseService } from '../../services/course-service/course.service';
import { AiAssistantComponent } from '../../components/ai-assistant/ai-assistant.component';

@Component({
  selector: 'app-course-detail',
  imports: [RouterLink, AiAssistantComponent],
  templateUrl: './course-detail.component.html',
  styleUrl: './course-detail.component.scss',
})
export class CourseDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private courseService = inject(CourseService);

  course = signal<Course | null>(null);
  loading = signal(true);
  error = signal(false);

  sectionsContext = computed(() => {
    const course = this.course();
    if (!course) return '';
    return [course.title, course.description, ...course.sections.map(s => s.content)]
      .filter(Boolean)
      .join('\n\n');
  });

  ngOnInit() {
    const courseId = Number(this.route.snapshot.paramMap.get('id'));
    this.courseService.getCourse(courseId).subscribe({
      next: (course) => {
        this.course.set(course);
        this.loading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.loading.set(false);
      }
    });
  }
}
