package org.chicdenver.data.model.ChicAssignments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Attachment;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseWork;
import com.google.api.services.classroom.model.Date;
import com.google.api.services.classroom.model.Invitation;
import com.google.api.services.classroom.model.Link;
import com.google.api.services.classroom.model.ModifyAttachmentsRequest;
import com.google.api.services.classroom.model.StudentSubmission;
import com.google.api.services.classroom.model.TimeOfDay;
import com.google.api.services.classroom.model.TurnInStudentSubmissionRequest;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import org.chicdenver.R;
import org.chicdenver.data.model.Grade;
import org.chicdenver.data.model.LoggedInUser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ChicCourse {

    private Context context;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Arrays.asList(ClassroomScopes.CLASSROOM_COURSES,
            ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS, ClassroomScopes.CLASSROOM_COURSEWORK_ME,
            ClassroomScopes.CLASSROOM_COURSES, DriveScopes.DRIVE, ClassroomScopes.CLASSROOM_ROSTERS);

    private Classroom classroomService;
    private Drive drive;

    private String courseId;
    private String courseName;

    private LoggedInUser user;
    private List<ChicAssignment> assignments = null;

    public ChicCourse(Context context, LoggedInUser user, String courseId) {

        this.context = context;
        this.courseId = courseId;
        this.user = user;
        NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        String APPLICATION_NAME = context.getResources().getString(R.string.app_name);

        classroomService = new Classroom.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                getCredentials(context))
                .setApplicationName(APPLICATION_NAME)
                .build();
        drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(context))
                .setApplicationName(APPLICATION_NAME)
                .build();

    }

    public void sendInvite(String email) {

        Invitation invitation = new Invitation();
        invitation.setCourseId(courseId);
        invitation.setUserId(email);
        invitation.setRole("STUDENT");

        try {
            classroomService.invitations().create(invitation).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ChicAssignment createAssignment(LoggedInUser user) {
        CourseWork content = new CourseWork();
        content.setCourseId(courseId);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        date.setMonth(cal.get(Calendar.MONTH) + 1);
        date.setDay(cal.get(Calendar.DAY_OF_MONTH));
        date.setYear(cal.get(Calendar.YEAR));
        content.setDueDate(date);
        content.setDueTime(new TimeOfDay().setHours(8));
        content.setDescription("Enter Description");
        content.setMaxPoints(100.0);
        content.setTitle("New Assignment");
        content.setWorkType("Assignment");
        content.setState("Published");
        ChicAssignment assignment = new ChicAssignment(content, user, classroomService, drive);
        return assignment;
    }

    public Boolean isActive() throws IOException {

        return classroomService.courses().get(courseId).execute().getCourseState().equals("ACTIVE");

    }

    public void update(ChicAssignment assignment) throws IOException {

        ArrayList<String> ids = new ArrayList<>();

        for (ChicAssignment temp: getAssignments()) {
            ids.add(temp.getAssignmentId());
        }
        try {
            classroomService.courses().courseWork().patch(courseId, assignment.getAssignmentId(),
                    assignment.getContent()).setUpdateMask("title,dueDate,maxPoints,description,dueTime")
                    .execute();
        }
        catch (NullPointerException e) {
            classroomService.courses().courseWork().create(courseId, assignment.getContent())
                    .execute();
        }
    }

    public List<ChicAssignment> getAssignments() {
        if(this.assignments != null)
            return assignments;
        assignments = new ArrayList<>();
        Log.d("Before", ":)");
        try {
            List<CourseWork> courseWorkList =
                    classroomService.courses().courseWork().list(courseId).execute()
                            .getCourseWork();
            if(courseWorkList != null)
                for (CourseWork work : courseWorkList) {
                    assignments.add(new ChicAssignment(work, user, classroomService, drive));
                }
        } catch (UserRecoverableAuthIOException e) {
            e.printStackTrace();
            context.startActivity(e.getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            ((Activity) context).runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Please accept email invite to classroom!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("After", ":)");
        return assignments;
    }

    public String getCourseId() {
        return courseId;
    }

    public ChicAssignment getNextAssignment() {

        List<ChicAssignment> allAssignments = getAssignments();

        final ChicAssignment[] nextAssignment = {null};

        allAssignments.forEach(
                new Consumer<ChicAssignment>() {
                    @Override
                    public void accept(ChicAssignment current) {

                        if (current.getJavaDueDate().after(new java.util.Date())) {
                            if (nextAssignment[0] == null)
                                nextAssignment[0] = current;
                            else if (current.getJavaDueDate()
                                    .before(nextAssignment[0].getJavaDueDate()))
                                nextAssignment[0] = current;
                        }

                    }
                }
        );

        return nextAssignment[0];

    }

    public String getOverallGrade() {

        List<ChicAssignment> assignments = getAssignments();
        List<ChicAssignment> gradedAssignments = new ArrayList<>();

        assignments.forEach(new Consumer<ChicAssignment>() {
            @Override
            public void accept(ChicAssignment assignment) {
                if(assignment.isGraded())
                    gradedAssignments.add(assignment);
            }
        });

        if(gradedAssignments.size() == 0)
            return new Grade(100f).getLetter();

        double sum = 0;
        double pts = 0;
        for (int i = 0; i < gradedAssignments.size(); i++) {
            ChicAssignment current = gradedAssignments.get(i);
            if (current.isGraded()) {
                sum += current.getGrade();
                pts += current.courseWork.getMaxPoints();
            }
        }

        return new Grade((float) (sum / pts) * 100).getLetter();

    }

    public String getCourseName() {

        return courseName;

    }

    public void setCourseName(String courseName) {

        this.courseName = courseName;

    }

    private static GoogleAccountCredential getCredentials(Context context) {

        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context,
                SCOPES
        );

        credential.setSelectedAccount(Objects.requireNonNull(
                GoogleSignIn.getLastSignedInAccount(context)
        ).getAccount());

        return credential;

    }

    public static Course createCourse(Context context, Course course) {

        String APPLICATION_NAME = context.getResources().getString(R.string.app_name);

        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        try {
            return (new Classroom.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                    getCredentials(context))
                    .setApplicationName(APPLICATION_NAME)
                    .build()
            .courses().create(course.setOwnerId("me")).execute());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return course;

    }

    public static class ChicAssignment {


        private String title;
        private Date dueDate;
        private String desc;
        private double maxPts;

        private Boolean isGraded;
        private CourseWork courseWork;

        private List<StudentSubmission> submissions = new ArrayList<>();
        private Classroom classroomService;
        private Drive driveService;
        private LoggedInUser user;
        private TimeOfDay time;

        public ChicAssignment(CourseWork courseWork, LoggedInUser user, Classroom classroomService,
                              Drive driveService) {

            this.title = courseWork.getTitle();
            this.courseWork = courseWork;
            this.classroomService = classroomService;
            this.desc = courseWork.getDescription();
            this.maxPts = courseWork.getMaxPoints();
            this.driveService = driveService;
            this.user = user;
            time = courseWork.getDueTime();
            this.dueDate = courseWork.getDueDate();

            isGraded = checkState();

        }

        private ChicAssignment(String title, Date dueDate, String desc, TimeOfDay time, Double maxPts) {
            this.title = title;
            this.dueDate = dueDate;
            this.desc = desc;
            this.courseWork = null;
            submissions = null;
            classroomService = null;
            driveService = null;
            this.time = time;
            this.maxPts = maxPts;

            isGraded = false;

        }

        public boolean checkState() {

            final boolean[] assignmentGraded = {false};
            Thread checkGradeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("CheckGradeThread", "Started");
                        List<StudentSubmission> studentSubmissions = classroomService.courses()
                                .courseWork().studentSubmissions().list(courseWork.getCourseId(),
                                        courseWork.getId()).execute().getStudentSubmissions();
                        assignmentGraded[0] =
                                (studentSubmissions.get(0).getState().equals("RETURNED"));
                        Log.d("CheckGradeThread", studentSubmissions.toString());
                    } catch (IOException | NullPointerException e ) {
                        e.printStackTrace();
                        assignmentGraded[0] = false;
                    }
                }
            });
            checkGradeThread.start();
            try {
                checkGradeThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                assignmentGraded[0] = false;
            }

            return assignmentGraded[0];

        }

        public String getCourseId() {

            return courseWork.getCourseId();

        }

        public String getAssignmentId() {

            return courseWork.getId();

        }

        public String getDesc() {
            return desc;
        }

        public void submit() {

            new Thread(() -> {
                try {
                    classroomService.courses().courseWork().studentSubmissions().turnIn(courseWork.getCourseId(),
                            courseWork.getId(), classroomService.courses().courseWork().studentSubmissions().list(courseWork.getCourseId(),
                                    courseWork.getId()).execute().getStudentSubmissions().get(0).getId(), new TurnInStudentSubmissionRequest())
                            .execute();
                } catch (IOException e) {
                    Log.e("SUBMISSION-ERROR", e.getMessage());
                }
            }).start();

        }

        public double getGrade() {

            final double[] grade = new double[1];

            Thread gradeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        grade[0] = classroomService.courses().courseWork().studentSubmissions().list(courseWork.getCourseId(),
                                courseWork.getId()).execute().getStudentSubmissions().get(0).getAssignedGrade();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            gradeThread.start();
            try {
                gradeThread.join();
                return grade[0];
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return 0;

        }

        public java.util.Date getJavaDueDate() {

            Calendar cal = Calendar.getInstance();

            cal.set(dueDate.getYear(), dueDate.getMonth() - 1, dueDate.getDay());

            return cal.getTime();

        }

        public String getPrettyDate() {

            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy", Locale.US);

            return formatter.format(getJavaDueDate());

        }

        public String getTitle() {
            return title;
        }

        public Date getDueDate() {
            return dueDate;
        }

        public Boolean isGraded() {

            return isGraded;

        }

        public String getDescription() {

            return desc;

        }

        public Double getPoints() {
            return maxPts;
        }

        public TimeOfDay getTime() {
            return time;
        }

        public String getDocumentLink() {

            Attachment attachment = null;

            try {
                attachment = classroomService.courses().courseWork().studentSubmissions().list(courseWork.getCourseId(),
                        courseWork.getId()).execute().getStudentSubmissions().get(0).getAssignmentSubmission().getAttachments()
                        .get(0);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }

            if (attachment == null || attachment.getLink() == null) {
                attachment = new Attachment();
                File content = new File();
                content.setMimeType("application/vnd.google-apps.document");
                content.setName(courseWork.getTitle() + ": " + user.getDisplayName(1));
                String link = null;
                try {
                    File temp = driveService.files().create(content).execute();
                    link = driveService.files().get(temp.getId()).setFields("webViewLink").execute().getWebViewLink();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                attachment.setLink(new Link().setUrl(link));
                StudentSubmission x = null;
                try {
                    List<StudentSubmission> y = classroomService.courses().courseWork().studentSubmissions().list(courseWork.getCourseId(),
                            courseWork.getId()).execute().getStudentSubmissions();
                    x = y.get(0);
                    classroomService.courses().courseWork().studentSubmissions().modifyAttachments(
                            courseWork.getCourseId(), courseWork.getId(), x.getId(),
                            new ModifyAttachmentsRequest().setAddAttachments(
                                    Collections.singletonList(attachment)
                            )
                    ).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return attachment.getLink().getUrl();

        }


        public String getRemainingTime() {

            Calendar cal = Calendar.getInstance();
            cal.setTime(getJavaDueDate());

            LocalDateTime localDueDate = LocalDateTime.ofInstant(cal.toInstant(),
                    ZoneId.systemDefault());

            long noOfDaysBetween = ChronoUnit.DAYS.between(LocalDateTime.now(), localDueDate);

            return String.valueOf(noOfDaysBetween);
        }

        public CourseWork getContent() {
            return courseWork;
        }

        public ChicAssignment setCourseWork(String title, Date dueDate, String desc, Double pts) {

            courseWork.setTitle(title);
            courseWork.setDueDate(dueDate);
            courseWork.setDescription(desc);
            courseWork.setMaxPoints(pts);

            return this;

        }

        public boolean isSubmitted() {

            String[] state = new String[1];

            Thread submission = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        state[0] = classroomService.courses().courseWork().studentSubmissions().list(courseWork.getCourseId(),
                                courseWork.getId()).execute().getStudentSubmissions().get(0).getState();
                    } catch (IOException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
            submission.start();
            try {
                submission.join();
                return state[0].equals("TURNED_IN");
            }
            catch (InterruptedException e)
            {
                return false;
            }

        }
    }

}
