package parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ParserService implements CommandLineRunner {
  private static final String MOVIES_MARKER = "MOVIES LIST";
  private static final int MOVIES_SKIPS = 2;
  private static final String RATINGS_MARKER = "MOVIE RATINGS REPORT";
  private static final int RATINGS_SKIPS = 2;
  private static final String ACTRESSES_MARKER = "THE ACTRESSES LIST";
  private static final int ACTRESS_SKIPS = 4;
  private static final String ACTOR_MARKER = "THE ACTORS LIST";
  private static final int ACTOR_SKIPS = 4;
  private static final int BUFFER_SIZE = 200;

  Logger log = LoggerFactory.getLogger(ParserService.class);

  private BufferedReader getFileReader(final String file, String pattern, int skipLines)
      throws IOException, FileNotFoundException {
    BufferedReader fileReader;
    // support compressed files
    if (file.endsWith(".gz")) {
      fileReader =
          new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
    } else if (file.endsWith(".zip")) {
      fileReader =
          new BufferedReader(new InputStreamReader(new ZipInputStream(new FileInputStream(file))));
    } else {
      fileReader = new BufferedReader(new FileReader(file));
    }

    String line = "";
    while (!pattern.equals(line)) {
      line = fileReader.readLine();
    }
    for (int i = 0; i < skipLines; i++) {
      line = fileReader.readLine();
    }

    return fileReader;
  }

  @Override
  public void run(String... args) throws Exception {
    final BufferedReader fileReader =
        getFileReader("C:\\Users\\Easley94\\Desktop\\New folder\\data\\movies.list\\movies.list",
            MOVIES_MARKER, MOVIES_SKIPS);

    final String readLine = fileReader.readLine();
    final String[] split = readLine.split("\"");
    final int i = split.length;
    final int count = StringUtils.countMatches(split[1], "#");

    final Stream<String> lines = fileReader.lines();
    lines
        .filter(line -> !(StringUtils.startsWith(line, "\"")))
        .filter(line -> StringUtils.isNotBlank(line))
        .filter(
            line -> (line.indexOf("(TV)") == -1) && (line.indexOf("{{") == -1)
                && (line.indexOf("(V)") == -1))
        .filter(
            line -> (line.substring(line.indexOf('\t')).trim().length() != 0)
            && (line.substring(line.indexOf('\t')).trim().charAt(0) != '?'))

        .forEach(
            line -> {
              log.info(line.substring(line.indexOf('\t')).trim().substring(0, 4) + "--"
                  + line.substring(0, line.indexOf('\t')).trim().replaceAll("\\(\\d*\\)", ""));
            });


    log.info("Hey");
  }
}
