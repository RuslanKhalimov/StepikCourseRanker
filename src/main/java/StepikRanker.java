import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StepikRanker {
    public List<String> getCourseRank(int n) throws RankerException {
        return getCourseRank(n, 60000, 60000);
    }

    public List<String> getCourseRank(int n, int readTimeout, int connectionTimeout) throws RankerException {
        if (n < 0) {
            throw new RankerException("Negative argument is not allowed : " + n);
        }
        List<Course> courses = new ArrayList<>();
        boolean hasNext = true;
        for (int page = 1; hasNext; page++) {
            try (HttpRequest httpRequest = new HttpRequest("https://stepik.org/api/courses?page=" + page, readTimeout, connectionTimeout)) {
                JsonReader reader = new JsonReader(httpRequest.getReader());

                reader.beginObject();
                while (reader.hasNext()) {
                    String header = reader.nextName();

                    switch (header) {
                        case "meta":
                            hasNext = metaInfo(reader);
                            break;
                        case "courses":
                            courses.addAll(readCoursesArray(reader));
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }
                }
                reader.endObject();
            } catch (IOException e) {
                throw new RankerException("Can't read from json object : " + e.getMessage(), e.getCause());
            } catch (Exception e) {
                throw new RankerException("Can't close reader or disconnect + : " + e.getMessage(), e.getCause());
            }
        }

        courses.sort((a, b) -> b.studentsCount - a.studentsCount);
        int rank = 1;
        List<String> result = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            if (i > 0 && courses.get(i).studentsCount != courses.get(i - 1).studentsCount) {
                rank = i + 1;
            }
            if (rank > n) {
                break;
            }
            result.add(rank + ") " + courses.get(i).courseName + " : " + courses.get(i).studentsCount + " students");
        }

        return result;
    }

    private boolean metaInfo(JsonReader reader) throws IOException {
        boolean result = false;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("has_next")) {
                result = reader.nextBoolean();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return result;
    }

    private List<Course> readCoursesArray(JsonReader reader) throws IOException {
        List<Course> result = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            result.add(readCourse(reader));
        }
        reader.endArray();
        return result;
    }

    private Course readCourse(JsonReader reader) throws IOException {
        String courseName = "";
        int studentsCount = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            switch (name) {
                case "title":
                    courseName = reader.nextString();
                    break;
                case "learners_count":
                    studentsCount = reader.nextInt();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return new Course(courseName, studentsCount);
    }

    private class Course {
        private final String courseName;
        private final int studentsCount;

        Course(String courseName, int studentsCount) {
            this.courseName = courseName;
            this.studentsCount = studentsCount;
        }

        @Override
        public String toString() {
            return courseName + " : " + studentsCount + " students";
        }
    }
}
