import assert from 'node:assert/strict';

export const ANSWER_DISPLAY_ROLES = ['support', 'target'];

const SUPPORT_PROMPT = /^(Que signifie |Que veut dire |Que demande |Que décrit |Que se passe-t-il |Que fait-on |Quand envoie-t-on |Que ne faut-il |Que ne dois-tu )/;

export function requireAnswerDisplayRole(role, path = 'answerDisplayRole') {
  assert.ok(ANSWER_DISPLAY_ROLES.includes(role), `${path}: answerDisplayRole must be support or target`);
  return role;
}

export function displayedAnswerText(question, answer) {
  const role = requireAnswerDisplayRole(question.answerDisplayRole, question.id);
  const text = answer?.text?.[role];
  assert.ok(typeof text === 'string' && text.trim().length > 0, `${answer?.id}: empty displayed answer`);
  return text;
}

/** True when a button would show both sides of a bilingual pair. */
export function leaksBothRoles(pair, displayed) {
  if (!pair || typeof displayed !== 'string') return true;
  const support = pair.support?.trim() ?? '';
  const target = pair.target?.trim() ?? '';
  const shown = displayed.trim();
  if (!support || !target || !shown) return true;
  if (support === target) return false;
  if (shown === support || shown === target) return false;
  return shown.includes(support) && shown.includes(target);
}

export function extractQuoted(prompt) {
  const match = String(prompt ?? '').match(/[«“‘']([^»”’']+)[»”’']/);
  return match ? match[1].trim() : '';
}

export function classifyQuestion(moduleId, question) {
  const fr = question.prompt?.support?.trim() ?? '';
  const quoted = extractQuoted(fr);
  const grammar = moduleId === 'module.grammar';
  if (SUPPORT_PROMPT.test(fr)) {
    const phrase = /\s/.test(quoted) || /[.?!]/.test(quoted);
    return {
      kind: grammar ? 'E' : phrase ? 'D' : 'A',
      role: 'support',
      inputRole: 'target',
      label: grammar ? 'E. Danish grammar (meaning)' : phrase ? 'D. Meaning of a Danish phrase' : 'A. Danish → French vocabulary'
    };
  }
  let kind = 'F';
  let label = 'F. Situational / communication';
  if (grammar) {
    kind = 'E';
    label = 'E. Danish grammar';
  } else if (/^Quelle phrase |^Comment dit-on |^Comment dites-vous «|^Que dit /.test(fr)) {
    kind = 'C';
    label = 'C. Choose the correct Danish phrase';
  } else if (/^Quel (mot|verbe|pronom) |^Quel est le mot |^Quelle paire |^Quelle structure |^Quelle construction |^Quel verbe /.test(fr)) {
    kind = 'B';
    label = 'B. French → Danish vocabulary';
  }
  return { kind, role: 'target', inputRole: 'support', label };
}

export function assertAnswerDisplay(question, path = question.id) {
  requireAnswerDisplayRole(question.answerDisplayRole, path);
  assert.ok(Array.isArray(question.answers) && question.answers.length >= 2, `${path}: missing answers`);
  const seen = new Set();
  for (const answer of question.answers) {
    const displayed = displayedAnswerText(question, answer);
    const key = displayed.trim();
    assert.ok(!seen.has(key), `${path}: duplicate displayed answer «${key}»`);
    seen.add(key);
    assert.equal(
      leaksBothRoles(answer.text, displayed),
      false,
      `${path}: answer button leaks both support and target (${answer.id})`
    );
    const bilingual = `${answer.text.target}\n${answer.text.support}`;
    assert.equal(
      leaksBothRoles(answer.text, bilingual),
      answer.text.support.trim() !== answer.text.target.trim(),
      `${path}: leakage detector must catch target+support buttons when sides differ`
    );
  }
  return question.answerDisplayRole;
}

export function withAnswerDisplayRole(question, role) {
  requireAnswerDisplayRole(role, question.id);
  const { id, order, type, prompt, answers, explanation, tags, answerDisplayRole: ignored, ...rest } = question;
  if (Object.keys(rest).length) {
    throw new Error(`${question.id}: unexpected question fields ${Object.keys(rest).join(', ')}`);
  }
  return { id, order, type, answerDisplayRole: role, prompt, answers, explanation, tags };
}
